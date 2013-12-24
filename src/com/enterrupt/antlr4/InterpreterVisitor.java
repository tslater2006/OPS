package com.enterrupt.antlr4;

import java.util.*;
import java.lang.reflect.*;
import com.enterrupt.types.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.misc.Interval;
import com.enterrupt.runtime.*;
import com.enterrupt.trace.*;
import org.apache.logging.log4j.*;
import com.enterrupt.antlr4.frontend.*;

class EvaluateConstruct {
	public MemoryPtr baseExpr;
	public boolean trueBranchExprSeen = false;
	public boolean breakSeen = false;
	public EvaluateConstruct(MemoryPtr p) { this.baseExpr = p; }
}

enum InterruptFlag {
	BREAK
}

public class InterpreterVisitor extends PeopleCodeBaseVisitor<Void> {

	private static Logger log = LogManager.getLogger(InterpreterVisitor.class.getName());

	private InterpretSupervisor supervisor;
	private ParseTreeProperty<MemoryPtr> exprValues = new ParseTreeProperty<MemoryPtr>();
	private CommonTokenStream tokens;
	private Stack<EvaluateConstruct> evalConstructStack = new Stack<EvaluateConstruct>();
	private InterruptFlag interrupt;

	public InterpreterVisitor(CommonTokenStream t, InterpretSupervisor s) {
		this.tokens = t;
		this.supervisor = s;
	}

	private void setExprValue(ParseTree node, MemoryPtr ptr) {
		this.exprValues.put(node, ptr);
	}

	private MemoryPtr getExprValue(ParseTree node) {
		if(this.exprValues.get(node) == null) {

			int lineNbr = -1;
			Object obj = node.getPayload();
			if(obj instanceof Token) {
				lineNbr = ((Token)node.getPayload()).getLine();
			} else if(obj instanceof ParserRuleContext) {
				lineNbr = ((ParserRuleContext)node.getPayload()).getStart().getLine();
			} else {
				throw new EntVMachRuntimeException("Unexpected payload type.");
			}

			throw new EntInterpretException("Encountered attempt to get the value of an expression " +
				"that has yet to be evaluated", node.getText(), lineNbr);
		}
		return this.exprValues.get(node);
	}

	private void emitStmt(ParserRuleContext ctx) {
		StringBuffer line = new StringBuffer();
		Interval interval = ctx.getSourceInterval();

		int i = interval.a;
		while(i <= interval.b) {
			Token t = tokens.get(i);
			if(t.getChannel() == PeopleCodeLexer.REFERENCES) { i++; continue; }
			if(t.getText().contains("\n")) { break; }
			line.append(t.getText());
			i++;
		}

		TraceFileVerifier.submitEmission(new PCInstruction(line.toString()));
	}

	private void emitStmt(String str) {
		TraceFileVerifier.submitEmission(new PCInstruction(str));
	}

	public Void visitProgram(PeopleCodeParser.ProgramContext ctx) {
		this.supervisor.pushRefEnvi(new RefEnvi("ProgramLocal"));
		visit(ctx.stmtList());
		this.supervisor.popRefEnvi();
		return null;
	}

	/**********************************************************
	 * <stmt> alternative handlers.
	 **********************************************************/

	public Void visitStmtIf(PeopleCodeParser.StmtIfContext ctx) {
		this.emitStmt(ctx);

		// Get value of conditional expression.
		visit(ctx.ifStmt().expr());
		boolean exprResult = ((BooleanPtr) getExprValue(ctx.ifStmt().expr())).read();

		// If expression evaluates to true, visit the conditional body.
		if(exprResult) {
			visit(ctx.ifStmt().stmtList(0));
		}

		this.emitStmt("End-If");

		return null;
	}

	public Void visitStmtBreak(PeopleCodeParser.StmtBreakContext ctx) {
		this.emitStmt(ctx);
		this.interrupt = InterruptFlag.BREAK;
		return null;
	}

	public Void visitStmtAssign(PeopleCodeParser.StmtAssignContext ctx) {
		this.emitStmt(ctx);
		visit(ctx.expr(1));
		MemoryPtr srcOperand = getExprValue(ctx.expr(1));
		visit(ctx.expr(0));
		MemoryPtr destOperand = getExprValue(ctx.expr(0));
		MemoryPtr.copy(srcOperand, destOperand);
		return null;
	}

	public Void visitStmtExpr(PeopleCodeParser.StmtExprContext ctx) {
		this.emitStmt(ctx);
		visit(ctx.expr());
		return null;
	}

	/**********************************************************
	 * <expr> alternative handlers.
	 **********************************************************/

	public Void visitExprParenthesized(PeopleCodeParser.ExprParenthesizedContext ctx) {
		visit(ctx.expr());
		setExprValue(ctx, getExprValue(ctx.expr()));
		return null;
	}

	public Void visitExprLiteral(PeopleCodeParser.ExprLiteralContext ctx) {
		visit(ctx.literal());
		setExprValue(ctx, getExprValue(ctx.literal()));
		return null;
	}

	public Void visitExprId(PeopleCodeParser.ExprIdContext ctx) {
		visit(ctx.id());
		setExprValue(ctx, getExprValue(ctx.id()));
		return null;
	}

	public Void visitExprFnOrRowsetCall(PeopleCodeParser.ExprFnOrRowsetCallContext ctx) {

		/**
		 * TODO: Support rowset indexing; assuming function calls for now.
		 */

		// null is used to separate call frames.
		Environment.pushToCallStack(null);

		// if args exist, move args from runtime stack to call stack.
		if(ctx.exprList() != null) {
			for(PeopleCodeParser.ExprContext argCtx : ctx.exprList().expr()) {
				visit(argCtx);
				Environment.pushToCallStack(getExprValue(argCtx));
			}
		}

		// Get function reference using reflection.
		Method fnPtr = Environment.getSystemFunc(ctx.expr().getText());
		if(fnPtr == null) {
			throw new EntInterpretException("Encountered attempt to call unimplemented " +
				"system function", ctx.expr().getText(), ctx.expr().getStart().getLine());
		}

		// Invoke function.
        try {
            fnPtr.invoke(Environment.class);
        } catch(java.lang.IllegalAccessException iae) {
            log.fatal(iae.getMessage(), iae);
            System.exit(ExitCode.REFLECT_FAIL_SYS_FN_INVOCATION.getCode());
        } catch(java.lang.reflect.InvocationTargetException ite) {
            log.fatal(ite.getMessage(), ite);
            System.exit(ExitCode.REFLECT_FAIL_SYS_FN_INVOCATION.getCode());
        }

		/**
		 * Pop the first value from the call stack. If it's null, the function
		 * did not emit a return value. If it's non-null, the next item on the stack
		 * must be the null separator (PeopleCode funcs can only return 1 value).
		 */
	    MemoryPtr retPtr = Environment.popFromCallStack();
		setExprValue(ctx, retPtr);

		if(retPtr != null && (Environment.popFromCallStack() != null)) {
			throw new EntVMachRuntimeException("More than one return value was found on " +
				"the call stack.");
		}

		return null;
	}

	public Void visitExprMethodOrStaticRef(
					PeopleCodeParser.ExprMethodOrStaticRefContext ctx) {

		MemoryPtr ptr = Environment.getCompBufferEntry(ctx.getText());
		if(ptr == null) {
			throw new EntInterpretException("Encountered a reference to an " +
				"uninitialized component buffer field", ctx.getText(),
				ctx.getStart().getLine());
		}
		setExprValue(ctx, ptr);
		return null;
	}

	public Void visitExprEquality(PeopleCodeParser.ExprEqualityContext ctx) {
		visit(ctx.expr(0));
		MemoryPtr p1 = getExprValue(ctx.expr(0));
		visit(ctx.expr(1));
		MemoryPtr p2 = getExprValue(ctx.expr(1));

		MemoryPtr result;
		if(MemoryPtr.isEqual(p1, p2)) {
			result = Environment.TRUE;
		} else {
			result = Environment.FALSE;
		}
		setExprValue(ctx, result);
		log.debug("Compared for equality: {}, result={}", ctx.getText(), result);
		return null;
	}

	public Void visitExprBoolean(PeopleCodeParser.ExprBooleanContext ctx) {

		if(ctx.op.getText().equals("Or")) {
			visit(ctx.expr(0));
			BooleanPtr lhs = (BooleanPtr)getExprValue(ctx.expr(0));

			/**
			 * Short-circuit evaluation: if lhs is true, this expression is true,
			 * otherwise evaluate rhs and bubble up its value.
			 */
			if(lhs.read()) {
				setExprValue(ctx, lhs);
			} else {
				visit(ctx.expr(1));
				setExprValue(ctx, getExprValue(ctx.expr(1)));
			}
		} else {
			throw new EntInterpretException("Unsupported boolean comparison operation",
				ctx.getText(), ctx.getStart().getLine());
		}

		return null;
	}

	/**********************************************************
	 * Primary rule handlers.
	 **********************************************************/

	public Void visitId(PeopleCodeParser.IdContext ctx) {

		if(ctx.SYS_VAR_ID() != null) {
			MemoryPtr ptr = Environment.getSystemVar(ctx.getText());
			if(ptr == null) {
				throw new EntInterpretException("Encountered a system variable reference " +
					"that has not been implemented yet", ctx.getText(), ctx.getStart().getLine());
			}
			setExprValue(ctx, ptr);

		} else if(ctx.VAR_ID() != null) {
			MemoryPtr ptr = this.supervisor.resolveIdentifierToPtr(ctx.getText());
		} else {
			throw new EntVMachRuntimeException("Need to support GENERIC_IDs as identifiers.");
		}
		return null;
	}

	public Void visitLiteral(PeopleCodeParser.LiteralContext ctx) {

		if(ctx.defnLiteral() != null) {

			MemoryPtr ptr = Environment.getFromLiteralPool(
				ctx.defnLiteral().GENERIC_ID().getText());
			setExprValue(ctx, ptr);

		} else if(ctx.IntegerLiteral() != null) {

			MemoryPtr ptr = Environment.getFromLiteralPool(
				new Integer(ctx.IntegerLiteral().getText()));
			setExprValue(ctx, ptr);

		} else if(ctx.boolLiteral() != null) {

			String b = ctx.boolLiteral().getText();
			if(b.equals("True") || b.equals("true")) {
				setExprValue(ctx, Environment.TRUE);
			} else {
				setExprValue(ctx, Environment.FALSE);
			}

		} else if(ctx.DecimalLiteral() != null) {
			throw new EntVMachRuntimeException("Encountered a decimal literal; need to create "
				+ "a BigDecimal memory pool and type.");
		} else {
			throw new EntVMachRuntimeException("Unable to resolve literal to a terminal node.");
		}

		return null;
	}

	public Void visitVarDeclaration(PeopleCodeParser.VarDeclarationContext ctx) {

		String scope = ctx.varScope().getText();

		List<PeopleCodeParser.VarDeclaratorContext> varsToDeclare = ctx.varDeclarator();
		String[] ids = new String[varsToDeclare.size()];
		int i = 0;
		for(PeopleCodeParser.VarDeclaratorContext idCtx : varsToDeclare) {
			ids[i] = idCtx.VAR_ID().getText();
			if(idCtx.expr() != null) {
				throw new EntVMachRuntimeException("Initialization during var declaration is " +
					"not yet supported.");
			}
			i++;
		}

		if(ctx.varType().varType() != null) {
			//throw new EntVMachRuntimeException("Declaring array object vars " +
			//	"is not yet supported.");
		} else if(ctx.varType().appClassPath() != null) {
			//throw new EntVMachRuntimeException("Declaring app class object vars " +
			//	"is not yet supported.");
		} else {
/*			String id = ctx.varType().GENERIC_ID().getText();
			switch(id) {
				case "Record":
					this.assignVarToCorrectRefEnvi(scope, id);
					break;
				default:
					throw new EntVMachRuntimeException("Unexpected variable type (" +
						id + ") encountered in declaration.");
			}*/
		}

		return null;
	}

	public Void visitEvaluateStmt(PeopleCodeParser.EvaluateStmtContext ctx) {
		this.emitStmt(ctx);

		visit(ctx.expr());
		EvaluateConstruct evalConstruct = new EvaluateConstruct(
			getExprValue(ctx.expr()));
		this.evalConstructStack.push(evalConstruct);

		List<PeopleCodeParser.WhenBranchContext> branches = ctx.whenBranch();
		for(PeopleCodeParser.WhenBranchContext branchCtx : branches) {
			visit(branchCtx);
			if(this.interrupt == InterruptFlag.BREAK) {
				evalConstruct.breakSeen = true;
				this.interrupt = null;
				break;
			}
		}

		if(!evalConstruct.breakSeen && ctx.whenOtherBranch() != null) {
			visit(ctx.whenOtherBranch());
		}

		this.evalConstructStack.pop();

		return null;
	}

	public Void visitWhenBranch(PeopleCodeParser.WhenBranchContext ctx) {

		if(ctx.op != null) {
			throw new EntVMachRuntimeException("Encountered relational op in when "+
				"branch, not yet supported.");
		}

		EvaluateConstruct evalConstruct = this.evalConstructStack.peek();
		MemoryPtr p1 = evalConstruct.baseExpr;
		visit(ctx.expr());
		MemoryPtr p2 = getExprValue(ctx.expr());

		/**
		 * If a previous branch evaluated to true and we're here, that means
		 * execution continues to fall through all branches until either a break
		 * is seen, or the evaluate construct ends.
		 */
		if(evalConstruct.trueBranchExprSeen || MemoryPtr.isEqual(p1, p2)) {
			if(!evalConstruct.trueBranchExprSeen) {
				// Only emit the first true branch expr seen.
				this.emitStmt(ctx);
			}
			evalConstruct.trueBranchExprSeen = true;
			visit(ctx.stmtList());
		}

		return null;
	}

	public Void visitWhenOtherBranch(PeopleCodeParser.WhenOtherBranchContext ctx) {
		this.emitStmt(ctx);
		visit(ctx.stmtList());
		return null;
	}

	private void assignVarToCorrectRefEnvi(String scope, String id) {
		switch(scope) {
			case "Local":
				this.supervisor.assignLocalVar(id, null);
				break;
			case "Component":
				RefEnvi.assignComponentVar(id, null);
				break;
			case "Global":
				RefEnvi.assignGlobalVar(id, null);
				break;
			default:
				throw new EntVMachRuntimeException("Encountered unexpected variable " +
					" scope: " + scope);
		}
	}
}

