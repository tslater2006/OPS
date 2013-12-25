package com.enterrupt.antlr4;

import java.util.*;
import java.lang.reflect.*;
import com.enterrupt.types.*;
import com.enterrupt.pt.*;
import com.enterrupt.pt.peoplecode.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.misc.Interval;
import com.enterrupt.runtime.*;
import com.enterrupt.trace.*;
import com.enterrupt.scope.*;
import org.apache.logging.log4j.*;
import com.enterrupt.antlr4.frontend.*;

class EvaluateConstruct {
	public PTDataType baseExpr;
	public boolean trueBranchExprSeen = false;
	public boolean breakSeen = false;
	public EvaluateConstruct(PTDataType p) { this.baseExpr = p; }
}

enum InterruptFlag {
	BREAK
}

public class InterpreterVisitor extends PeopleCodeBaseVisitor<Void> {

	private ExecContext eCtx;
	private CommonTokenStream tokens;
	private InterpretSupervisor supervisor;

	private ParseTreeProperty<MemPointer> memPointers;
	private Stack<EvaluateConstruct> evalConstructStack;
	private InterruptFlag interrupt;
	private IEmission lastEmission;

	private static Logger log = LogManager.getLogger(InterpreterVisitor.class.getName());

	public InterpreterVisitor(ExecContext e, InterpretSupervisor s) {
		this.eCtx = e;
		this.tokens = e.prog.tokenStream;
		this.supervisor = s;
		this.memPointers = new ParseTreeProperty<MemPointer>();
		this.evalConstructStack = new Stack<EvaluateConstruct>();
	}

	private void setMemPointer(ParseTree node, MemPointer ptr) {
		this.memPointers.put(node, ptr);
	}

	private MemPointer getMemPointer(ParseTree node) {
		if(this.memPointers.get(node) == null) {

			int lineNbr = -1;
			Object obj = node.getPayload();
			if(obj instanceof Token) {
				lineNbr = ((Token)node.getPayload()).getLine();
			} else if(obj instanceof ParserRuleContext) {
				lineNbr = ((ParserRuleContext)node.getPayload()).getStart().getLine();
			} else {
				throw new EntVMachRuntimeException("Unexpected payload type.");
			}

			throw new EntInterpretException("Attempted to get the memory pointer for " +
				"for a node, encountered null: ", node.getText(), lineNbr);
		}

		return this.memPointers.get(node);
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

		IEmission e = new PCInstruction(line.toString());
		TraceFileVerifier.submitEmission(e);
		this.lastEmission = e;
	}

	private void emitStmt(String str) {
		IEmission e = new PCInstruction(str);
		TraceFileVerifier.submitEmission(e);
		this.lastEmission = e;
	}

	private void repeatLastEmission() {
		TraceFileVerifier.submitEmission(this.lastEmission);
	}

	public Void visitProgram(PeopleCodeParser.ProgramContext ctx) {

		/**
		 * App class programs do not get a fresh ref envi b/c they
		 * are always loaded to manipulate an existing object's ref envi, which
		 * has already been placed on the ref envi stack for this exec context. All
		 * other programs get a fresh program-local referencing environment.
		 */
		if(!(this.eCtx.prog instanceof AppClassPeopleCodeProg)) {
			this.eCtx.pushRefEnvi(new LocalRefEnvi(LocalRefEnvi.Type.PROGRAM_LOCAL));
		}

		visit(ctx.stmtList());
		this.eCtx.popRefEnvi();
		return null;
	}

	/**********************************************************
	 * <stmt> alternative handlers.
	 **********************************************************/

	public Void visitStmtIf(PeopleCodeParser.StmtIfContext ctx) {
		this.emitStmt(ctx);

		// Get value of conditional expression.
		visit(ctx.ifStmt().expr());
		boolean exprResult = ((PTBoolean) getMemPointer(ctx.ifStmt()
			.expr()).dereference()).value();

		// If expression evaluates to true, visit the conditional body;
		// otherwise, visit the Else body if it exists.
		if(exprResult) {
			visit(ctx.ifStmt().stmtList(0));
			this.emitStmt("End-If");
		} else {
			if(ctx.ifStmt().stmtList(1) != null) {
				visit(ctx.ifStmt().stmtList(1));
			}
		}

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
		MemPointer target = getMemPointer(ctx.expr(1));
		visit(ctx.expr(0));
		MemPointer pointer = getMemPointer(ctx.expr(0));
		pointer.assign(target.dereference());
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
		setMemPointer(ctx, getMemPointer(ctx.expr()));
		return null;
	}

	public Void visitExprLiteral(PeopleCodeParser.ExprLiteralContext ctx) {
		visit(ctx.literal());
		setMemPointer(ctx, getMemPointer(ctx.literal()));
		return null;
	}

	public Void visitExprId(PeopleCodeParser.ExprIdContext ctx) {
		visit(ctx.id());
		/**
		 * GENERIC_IDs could be reserved words (i.e., MenuName);
		 * do not attempt to bubble up any values for them at this time.
		 */
		if(ctx.id().SYS_VAR_ID() != null ||
			ctx.id().VAR_ID() != null) {
			setMemPointer(ctx, getMemPointer(ctx.id()));
		}
		return null;
	}

	public Void visitExprFnOrIdxCall(PeopleCodeParser.ExprFnOrIdxCallContext ctx) {

		visit(ctx.expr());

		// null is used to separate call frames.
		Environment.pushToCallStack(null);

		// if args exist, move args from runtime stack to call stack.
		if(ctx.exprList() != null) {
			visit(ctx.exprList());
			for(PeopleCodeParser.ExprContext argCtx : ctx.exprList().expr()) {
				visit(argCtx);
				Environment.pushToCallStack(getMemPointer(argCtx));
			}
		}

		Method fnPtr;
		if(ctx.expr() instanceof PeopleCodeParser.ExprIdContext) {

			PeopleCodeParser.ExprIdContext exprCtx =
				(PeopleCodeParser.ExprIdContext)ctx.expr();

			if(exprCtx.id().GENERIC_ID() != null) {
				/**
				 * This is a system function, i.e. CreateRecord; get
				 * a reference to the function using reflection.
				 */
				fnPtr = Environment.getSystemFunc(exprCtx.id().GENERIC_ID().getText());

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

			} else if(exprCtx.id().VAR_ID() != null) {
				/**
				 * This is an index into a rowset, i.e. &rs(1)
				 */
				throw new EntVMachRuntimeException("Rowset indexing is not supported " +
					"at this time.");
			} else if(exprCtx.id().SYS_VAR_ID() != null) {
				 throw new EntVMachRuntimeException("Did not expect system var " +
					"to immediately function expression list.");
			} else {
				throw new EntVMachRuntimeException("Encountered unknown ExprIdContext " +
					"immediately preceding the expression list of a function call.");
			}
		} else if(ctx.expr() instanceof
					PeopleCodeParser.ExprDotAccessContext) {

			/**
			 * Call the method name in .id() on the memory pointer represented
			 * by .expr().
			 * TODO: Will need to support method calls on objects other than
			 * app class objects (i.e., Fields, Records, etc.).
			 */
			PeopleCodeParser.ExprDotAccessContext exprCtx =
				(PeopleCodeParser.ExprDotAccessContext)ctx.expr();

			String methodName = exprCtx.id().getText();
			MemPointer objPointer = getMemPointer(exprCtx.expr());
			PTAppClassObject obj = (PTAppClassObject)objPointer.dereference();

			/**
			 * Record field references beyond a certain recursion level
			 * in app class programs have their constituent record defns
			 * loaded lazily. Before executing the app class method, check
			 * if any of the references within programs referenced by the app class
			 * have yet to have their record defns loaded (note that they may
			 * already be loaded anyway if they were referenced elsewhere).
			 */
			for(PeopleCodeProg p : obj.progDefn.referencedProgs) {
				for(Map.Entry<Integer, Reference> cursor : p.progRefsTable.entrySet()) {
					if(cursor.getValue().isRecordFieldRef) {
						DefnCache.getRecord(cursor.getValue().RECNAME);
					}
				}
			}

			ExecContext methodCtx = new AppClassObjExecContext(obj, methodName);
			this.supervisor.runImmediately(methodCtx);

		} else {
			throw new EntInterpretException("Encountered unexpected expr type " +
				"preceding function call", ctx.expr().getText(),
					ctx.expr().getStart().getLine());
		}

		/**
		 * Pop the first value from the call stack. If it's null, the function
		 * did not emit a return value. If it's non-null, the next item on the stack
		 * must be the null separator (PeopleCode funcs can only return 1 value).
		 */
	    MemPointer ptr = Environment.popFromCallStack();
		setMemPointer(ctx, ptr);

		if(ptr != null && (Environment.popFromCallStack() != null)) {
			throw new EntVMachRuntimeException("More than one return value was found on " +
				"the call stack.");
		}

		return null;
	}

	public Void visitExprCreate(PeopleCodeParser.ExprCreateContext ctx) {
		visit(ctx.createInvocation());
		setMemPointer(ctx, getMemPointer(ctx.createInvocation()));
		return null;
	}

	public Void visitExprDotAccess(
					PeopleCodeParser.ExprDotAccessContext ctx) {
		visit(ctx.expr());

		/**
		 * Detect defn literals (i.e., MenuName.SA_LEARNER_SERVICES)
		 */
		if(ctx.expr() instanceof PeopleCodeParser.ExprIdContext &&
			PSDefn.defnLiteralReservedWordsTable.containsKey(
				((PeopleCodeParser.ExprIdContext)ctx.expr())
					.id().getText().toUpperCase())) {

            MemPointer ptr = Environment.getFromLiteralPool(
                ctx.id().getText());
   	        setMemPointer(ctx, ptr);
		} else {
			/**
			 * Otherwise, assume component buffer reference.
			 */
			MemPointer ptr = Environment.getCompBufferEntry(ctx.getText());
			if(ptr == null) {
				throw new EntInterpretException("Encountered a reference to an " +
					"uninitialized component buffer field", ctx.getText(),
					ctx.getStart().getLine());
			}
			setMemPointer(ctx, ptr);
		}

		return null;
	}

	public Void visitExprEquality(PeopleCodeParser.ExprEqualityContext ctx) {
		visit(ctx.expr(0));
		PTDataType p1 = getMemPointer(ctx.expr(0)).dereference();
		visit(ctx.expr(1));
		PTDataType p2 = getMemPointer(ctx.expr(1)).dereference();

		MemPointer result;
		if(p1.equals(p2)) {
			result = Environment.TRUE;
		} else {
			result = Environment.FALSE;
		}
		setMemPointer(ctx, result);
		log.debug("Compared for equality: {}, result={}", ctx.getText(), result.dereference());
		return null;
	}

	public Void visitExprBoolean(PeopleCodeParser.ExprBooleanContext ctx) {

		if(ctx.op.getText().equals("Or")) {
			visit(ctx.expr(0));
			PTBoolean lhs = (PTBoolean)getMemPointer(ctx.expr(0)).dereference();

			/**
			 * Short-circuit evaluation: if lhs is true, this expression is true,
			 * otherwise evaluate rhs and bubble up its value.
			 */
			if(lhs.value()) {
				setMemPointer(ctx, Environment.TRUE);
			} else {
				visit(ctx.expr(1));
				setMemPointer(ctx, getMemPointer(ctx.expr(1)));
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

	public Void visitMethodImpl(PeopleCodeParser.MethodImplContext ctx) {
		this.emitStmt(ctx);

		RefEnvi localRefEnvi = new LocalRefEnvi(LocalRefEnvi.Type.METHOD_LOCAL);
		List<String> formalParams = ((AppClassPeopleCodeProg)eCtx.prog).methodFormalParams.
			get(ctx.GENERIC_ID().getText());

		if(formalParams != null) {
			for(String formalParamId : formalParams) {
				localRefEnvi.declareVar(formalParamId, Environment.popFromCallStack());
			}
		}

		eCtx.pushRefEnvi(localRefEnvi);
		visit(ctx.stmtList());
		eCtx.popRefEnvi();

		this.emitStmt("end-method");
		return null;
	}

	public Void visitId(PeopleCodeParser.IdContext ctx) {

		if(ctx.SYS_VAR_ID() != null) {
			MemPointer ptr = Environment.getSystemVar(ctx.getText());
			if(ptr == null) {
				throw new EntInterpretException("Encountered a system variable reference " +
					"that has not been implemented yet", ctx.getText(), ctx.getStart().getLine());
			}
			setMemPointer(ctx, ptr);
		} else if(ctx.VAR_ID() != null) {
			MemPointer ptr = eCtx.resolveIdentifier(ctx.getText());
			setMemPointer(ctx, ptr);
		}

		return null;
	}

	public Void visitLiteral(PeopleCodeParser.LiteralContext ctx) {

		if(ctx.IntegerLiteral() != null) {

			MemPointer ptr = Environment.getFromLiteralPool(
				new Integer(ctx.IntegerLiteral().getText()));
			setMemPointer(ctx, ptr);

		} else if(ctx.BoolLiteral() != null) {

			String b = ctx.BoolLiteral().getText();
			if(b.equals("True") || b.equals("true")) {
				setMemPointer(ctx, Environment.TRUE);
			} else {
				setMemPointer(ctx, Environment.FALSE);
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

		String scope = ctx.varScope.getText();

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

		/**
		 * TODO: Convert all calls to new MemPointer to include
		 * declared type, in order to enforce type coherence.
		 */
		if(ctx.varType().varType() != null) {
			//throw new EntVMachRuntimeException("Declaring array object vars " +
			//	"is not yet supported.");
		} else if(ctx.varType().appClassPath() != null) {
			/**
			 * TODO: Will need to get the app class corresponding to the path
			 * and somehow provide that to MemPointer for type enforcement.
			 */
			for(String id : ids) {
				this.declareVar(scope, id, new MemPointer());
			}
		} else {
			String type = ctx.varType().GENERIC_ID().getText();
			for(String id : ids) {
				this.declareVar(scope, id, new MemPointer());
			}
		}

		return null;
	}

	public Void visitEvaluateStmt(PeopleCodeParser.EvaluateStmtContext ctx) {
		this.emitStmt(ctx);

		visit(ctx.expr());
		EvaluateConstruct evalConstruct = new EvaluateConstruct(
			getMemPointer(ctx.expr()).dereference());
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
		PTDataType p1 = evalConstruct.baseExpr;
		visit(ctx.expr());
		PTDataType p2 = getMemPointer(ctx.expr()).dereference();

		/**
		 * If a previous branch evaluated to true and we're here, that means
		 * execution continues to fall through all branches until either a break
		 * is seen, or the evaluate construct ends.
		 */
		if(evalConstruct.trueBranchExprSeen || p1.equals(p2)) {
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

	public Void visitCreateInvocation(PeopleCodeParser.CreateInvocationContext ctx) {

		AppClassPeopleCodeProg appClassProg;
		if(ctx.appClassPath() != null) {
			appClassProg = (AppClassPeopleCodeProg)DefnCache.getProgram(("AppClassPC." +
				ctx.appClassPath().getText() + ".OnExecute").replaceAll(":","."));
		} else {
			throw new EntVMachRuntimeException("Encountered create invocation without " +
				"app class prefix; need to support this by resolving path to class.");
		}

		PTAppClassObject appClassObj = new PTAppClassObject(appClassProg);
		setMemPointer(ctx, new MemPointer(appClassObj));

		/**
	     * Check for constructor; call it if it exists.
		 * TODO: If issues arise with this, I probably need to check the method
		 * declaration to ensure that the constructor (if it exists) is only called
	     * when the appropriate number of arguments are supplied.
		 */
		String constructorName = appClassProg.getClassName();
		if(appClassProg.methodEntryPoints.containsKey(constructorName)) {

			/**
			 * Load arguments to constructor onto call stack if
			 * args have been provided.
			 */
			if(ctx.exprList() != null) {
				visit(ctx.exprList());
				for(PeopleCodeParser.ExprContext argCtx : ctx.exprList().expr()) {
					visit(argCtx);
	                Environment.pushToCallStack(getMemPointer(argCtx));
				}
			}

			ExecContext constructorCtx = new AppClassObjExecContext(appClassObj,
				constructorName);
			this.supervisor.runImmediately(constructorCtx);
			this.repeatLastEmission();
		}
		return null;
	}

	private void declareVar(String scope, String id, MemPointer ptr) {
		switch(scope) {
			case "Local":
				eCtx.declareLocalVar(id, ptr);
				break;
			case "Component":
				RefEnvi.declareComponentVar(id, ptr);
				break;
			case "Global":
				RefEnvi.declareGlobalVar(id, ptr);
				break;
			default:
				throw new EntVMachRuntimeException("Encountered unexpected variable " +
					" scope: " + scope);
		}
	}
}

