package com.enterrupt.antlr4;

import java.util.*;
import java.lang.reflect.*;
import com.enterrupt.types.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import com.enterrupt.runtime.*;
import org.apache.logging.log4j.*;
import com.enterrupt.antlr4.frontend.*;

public class InterpreterVisitor extends PeopleCodeBaseVisitor<Void> {

	private static Logger log = LogManager.getLogger(InterpreterVisitor.class.getName());

	private ParseTreeProperty<MemoryPtr> exprValues = new ParseTreeProperty<MemoryPtr>();

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

	/**********************************************************
	 * <stmt> alternative handlers.
	 **********************************************************/

	public Void visitStmtIf(PeopleCodeParser.StmtIfContext ctx) {

		// Get value of conditional expression.
		visit(ctx.ifStmt().expr());
		boolean exprResult = ((BooleanPtr) getExprValue(ctx.ifStmt().expr())).read();

		// If expression evaluates to true, visit the conditional body.
		if(exprResult) {
			visit(ctx.ifStmt().stmtList(0));
		}

		return null;
	}

	public Void visitStmtEvaluate(PeopleCodeParser.StmtEvaluateContext ctx) {
		/** TODO: IMPLEMENT */
		return null;
	}

	public Void visitStmtAssign(PeopleCodeParser.StmtAssignContext ctx) {
		log.debug("Executing assignment: {}", ctx.getText());
		visit(ctx.expr(1));
		MemoryPtr srcOperand = getExprValue(ctx.expr(1));
		visit(ctx.expr(0));
		MemoryPtr destOperand = getExprValue(ctx.expr(0));
		MemoryPtr.copy(srcOperand, destOperand);
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
		Interpreter.pushToCallStack(null);

		// if args exist, move args from runtime stack to call stack.
		if(ctx.exprList() != null) {
			for(PeopleCodeParser.ExprContext argCtx : ctx.exprList().expr()) {
				visit(argCtx);
				Interpreter.pushToCallStack(getExprValue(argCtx));
			}
		}

		// Get function reference using reflection.
		Method fnPtr = RunTimeEnvironment.systemFuncTable.get(ctx.expr().getText());
		if(fnPtr == null) {
			throw new EntInterpretException("Encountered attempt to call unimplemented " +
				"system function", ctx.expr().getText(), ctx.expr().getStart().getLine());
		}

		// Invoke function.
        try {
            fnPtr.invoke(RunTimeEnvironment.class);
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
	    MemoryPtr retPtr = Interpreter.popFromCallStack();
		setExprValue(ctx, retPtr);

		if(retPtr != null && (Interpreter.popFromCallStack() != null)) {
			throw new EntVMachRuntimeException("More than one return value was found on " +
				"the call stack.");
		}

		return null;
	}

	public Void visitExprMethodOrStaticRef(
					PeopleCodeParser.ExprMethodOrStaticRefContext ctx) {

		MemoryPtr ptr = RunTimeEnvironment.getCompBufferEntry(ctx.getText());
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
			result = RunTimeEnvironment.TRUE;
		} else {
			result = RunTimeEnvironment.FALSE;
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
			MemoryPtr ptr = RunTimeEnvironment.systemVarTable.get(ctx.getText());
			if(ptr == null) {
				throw new EntInterpretException("Encountered a system variable reference " +
					"that has not been implemented yet", ctx.getText(), ctx.getStart().getLine());
			}
			setExprValue(ctx, ptr);

		} else if(ctx.VAR_ID() != null) {

			throw new EntVMachRuntimeException("Need to support references to non-system vars.");
		} else {
			/**
			 * TODO: What about GENERIC_ID?
			 */
		}
		return null;
	}

	public Void visitLiteral(PeopleCodeParser.LiteralContext ctx) {

		if(ctx.defnLiteral() != null) {

			MemoryPtr ptr = RunTimeEnvironment.getFromMemoryPool(
				ctx.defnLiteral().GENERIC_ID().getText());
			setExprValue(ctx, ptr);

		} else if(ctx.IntegerLiteral() != null) {

			MemoryPtr ptr = RunTimeEnvironment.getFromMemoryPool(
				new Integer(ctx.IntegerLiteral().getText()));
			setExprValue(ctx, ptr);

		} else if(ctx.boolLiteral() != null) {

			String b = ctx.boolLiteral().getText();
			if(b.equals("True") || b.equals("true")) {
				setExprValue(ctx, RunTimeEnvironment.TRUE);
			} else {
				setExprValue(ctx, RunTimeEnvironment.FALSE);
			}

		} else if(ctx.DecimalLiteral() != null) {
			throw new EntVMachRuntimeException("Encountered a decimal literal; need to create "
				+ "a BigDecimal memory pool and type.");
		} else {
			throw new EntVMachRuntimeException("Unable to resolve literal to a terminal node.");
		}

		return null;
	}
}
