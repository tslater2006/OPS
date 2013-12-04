package com.enterrupt.antlr4;

import java.util.*;
import java.lang.reflect.*;
import com.enterrupt.types.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import com.enterrupt.interpreter.*;
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
			throw new EntInterpretException("Encountered attempt to get the value of an expression " +
				"that has yet to be evaluated: " + node.getText());
		}
		return this.exprValues.get(node);
	}

	/**********************************************************
	 * <stmt> alternative handlers.
	 **********************************************************/

	public Void visitStmtIf(PeopleCodeParser.StmtIfContext ctx) {

		// Get value of conditional expression.
		visit(ctx.ifConstruct().expr());
		boolean exprResult = ((BooleanPtr) getExprValue(ctx.ifConstruct().expr())).read();

		// If expression evaluates to true, visit the conditional body.
		if(exprResult) {
			visit(ctx.ifConstruct().stmtList());
		}

		return null;
	}

	public Void visitStmtFnCall(PeopleCodeParser.StmtFnCallContext ctx) {
		// Visit (execute) the fn call, but no need to save any return value.
		visit(ctx.fnCall());
		return null;
	}

	public Void visitStmtAssign(PeopleCodeParser.StmtAssignContext ctx) {
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

	public Void visitExprDefnRef(PeopleCodeParser.ExprDefnRefContext ctx) {
		MemoryPtr ptr = RunTimeEnvironment.getFromMemoryPool(ctx.defnRef().OBJECT_ID().getText());
		setExprValue(ctx, ptr);
		return null;
	}

	public Void visitExprLiteral(PeopleCodeParser.ExprLiteralContext ctx) {
		visit(ctx.literal());
		setExprValue(ctx, getExprValue(ctx.literal()));
		return null;
	}

	public Void visitExprCompBufferRef(PeopleCodeParser.ExprCompBufferRefContext ctx) {
		MemoryPtr ptr = RunTimeEnvironment.compBufferTable.get(ctx.getText());
		if(ptr == null) {
			throw new EntInterpretException("Encountered a reference to an " +
				"uninitialized component buffer field.");
		}
		setExprValue(ctx, ptr);
		return null;
	}

	public Void visitExprSystemVar(PeopleCodeParser.ExprSystemVarContext ctx) {
		MemoryPtr ptr = RunTimeEnvironment.systemVarTable.get(ctx.getText());
		if(ptr == null) {
			throw new EntInterpretException("Encountered a system variable reference " +
				"that has not been implemented yet: " + ctx.SYS_VAR_ID().getText());
		}
		setExprValue(ctx, ptr);
		return null;
	}

	public Void visitExprFnCall(PeopleCodeParser.ExprFnCallContext ctx) {
		visit(ctx.fnCall());
		setExprValue(ctx, getExprValue(ctx.fnCall()));
		return null;
	}

	public Void visitExprComparison(PeopleCodeParser.ExprComparisonContext ctx) {
		visit(ctx.expr(0));
		MemoryPtr p1 = getExprValue(ctx.expr(0));
		visit(ctx.expr(1));
		MemoryPtr p2 = getExprValue(ctx.expr(1));

		if(MemoryPtr.isEqual(p1, p2)) {
			setExprValue(ctx, RunTimeEnvironment.TRUE);
		} else {
			setExprValue(ctx, RunTimeEnvironment.FALSE);
		}
		return null;
	}

	/**********************************************************
	 * Primary rule handlers.
	 **********************************************************/

	public Void visitFnCall(PeopleCodeParser.FnCallContext ctx) {

		// null is used to separate call frames.
		Interpreter.pushToCallStack(null);

		// move args from runtime stack to call stack.
		for(PeopleCodeParser.ExprContext exprCtx : ctx.exprList().expr()) {
			visit(exprCtx);
			Interpreter.pushToCallStack(getExprValue(exprCtx));
		}

		// Get function reference using reflection.
		Method fnPtr = RunTimeEnvironment.systemFuncTable.get(ctx.FUNC_ID().getText());
		if(fnPtr == null) {
			throw new EntInterpretException("Encountered attempt to call unimplemented " +
				"system function: " + ctx.FUNC_ID().getText());
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
			throw new EntInterpretException("More than one return value was found on " +
				"the call stack.");
		}

		return null;
	}

	public Void visitLiteral(PeopleCodeParser.LiteralContext ctx) {
		if(ctx.IntegerLiteral() != null) {

			MemoryPtr ptr = RunTimeEnvironment.getFromMemoryPool(
				new Integer(ctx.IntegerLiteral().getText()));
			setExprValue(ctx, ptr);

		} else if(ctx.booleanLiteral() != null) {

			String b = ctx.booleanLiteral().getText();
			if(b.equals("True") || b.equals("true")) {
				setExprValue(ctx, RunTimeEnvironment.TRUE);
			} else {
				setExprValue(ctx, RunTimeEnvironment.FALSE);
			}

		} else if(ctx.DecimalLiteral() != null) {
			throw new EntInterpretException("Encountered a decimal literal; need to create "
				+ "a BigDecimal memory pool and type.");
		} else {
			throw new EntInterpretException("Unable to resolve literal to a terminal node.");
		}

		return null;
	}
}
