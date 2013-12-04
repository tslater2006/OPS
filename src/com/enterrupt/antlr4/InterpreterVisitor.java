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

public class InterpreterVisitor extends PeopleCodeBaseVisitor<MemoryPtr> {

	private static Logger log = LogManager.getLogger(InterpreterVisitor.class.getName());

	private ParseTreeProperty<MemoryPtr> exprValues = new ParseTreeProperty<MemoryPtr>();

	private void setExprValue(PeopleCodeParser.ExprContext ctx, MemoryPtr ptr) {
		this.exprValues.put(ctx, ptr);
	}

	private MemoryPtr getExprValue(PeopleCodeParser.ExprContext ctx) {
		if(this.exprValues.get(ctx) == null) {
			throw new EntInterpretException("Encountered attempt to get the value of an expression " +
				"that has yet to be evaluated.");
		}
		return this.exprValues.get(ctx);
	}

	public MemoryPtr visitIfStmt(PeopleCodeParser.IfStmtContext ctx) {

		// Get value of conditional expression.
		visit(ctx.expr());
		boolean exprResult = ((BooleanPtr) Interpreter.popFromRuntimeStack()).read();

		// If expression evaluates to true, visit the conditional body.
		if(exprResult) {
			visit(ctx.stmtList());
		}

		return null;
	}

/*	public MemoryPtr visitFnCall(PeopleCodeParser.FnCallContext ctx) {

		// null is used to separate call frames.
		Interpreter.pushToCallStack(null);

		// move args from runtime stack to call stack.
		for(PeopleCodeParser.ExprContext exprCtx : ctx.expr()) {
			visit(exprCtx);
			Interpreter.pushToCallStack(getExprValue(exprCtx));
		}

		// Get function reference using reflection.
		Method fnPtr = RunTimeEnvironment.systemFuncTable.get(ctx.FN_NAME().getText());
		if(fnPtr == null) {
			throw new EntInterpretException("Encountered attempt to call unimplemented " +
				"system function: " + ctx.FN_NAME().getText());
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
*/
		/**
		 * Pop the first value from the call stack. If it's null, the function
		 * did not emit a return value. If it's non-null, the next item on the stack
		 * must be the null separator (PeopleCode funcs can only return 1 value).
		 */
/*	    MemoryPtr retPtr = Interpreter.popFromCallStack();
		if(retPtr != null && (Interpreter.popFromCallStack() != null)) {
			throw new EntInterpretException("More than one return value was found on " +
				"the call stack.");
		}

		return retPtr;
	}

	public MemoryPtr visitCBufferRef(PeopleCodeParser.CBufferRefContext ctx) {

		MemoryPtr ptr = RunTimeEnvironment.compBufferTable.get(ctx.getText());
		if(ptr == null) {
			throw new EntInterpretException("Encountered a reference to an " +
				"uninitialized component buffer field.");
		}

		return ptr;
	}*/
}
