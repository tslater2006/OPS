package com.enterrupt.runtime;

import java.util.*;
import java.io.*;
import java.nio.charset.Charset;
import com.enterrupt.runtime.*;
import com.enterrupt.trace.*;
import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.types.*;
import org.apache.logging.log4j.*;
import com.enterrupt.antlr4.*;

public class InterpretSupervisor {

	private LinkedList<ExecContext> execContextStack;

	private static Logger log = LogManager.getLogger(InterpretSupervisor.class.getName());

	public InterpretSupervisor(ExecContext e) {
		this.execContextStack = new LinkedList<ExecContext>();
		this.execContextStack.push(e);
	}

	public void run() {

		this.runTopOfStack();

		if(this.execContextStack.size() != 0) {
			throw new EntVMachRuntimeException("Expected exec context stack to be empty.");
		}

		if(Environment.getCallStackSize() != 0) {
			throw new EntVMachRuntimeException("Expected call stack to be empty.");
		}
	}

	private void runTopOfStack() {

		ExecContext context = execContextStack.peek();

		boolean doEmitProgramMarkers = true;

		/**
		 * If another context is on the stack, it means that context was
		 * interrupted by the call to run this one. If the programs being run in
		 * both contexts are the same, PCStart/PCBegin/PCEnd markers should not
		 * be emitted.
		 */
		if(execContextStack.size() > 1) {
			// get the context that was interrupted by the call to run this context.
			ExecContext prevContext = execContextStack.get(1);
			if(prevContext.prog.getDescriptor().equals(context.prog.getDescriptor())) {
				doEmitProgramMarkers = false;
			}
		}

		context.prog.loadDefnsAndPrograms();

		/**
		 * Requests to load app class declaration bodies do not
		 * involve execution of PeopleCode instructions, and thus should
		 * not trigger any trace file emissions.
		 */
		if(context instanceof AppClassDeclExecContext) {
			InterpreterVisitor interpreter = new InterpreterVisitor(context, this);
			interpreter.visit(context.startNode);
			execContextStack.pop();
			return;
		}

		String methodOrFuncName = "";
		if(context instanceof ProgramExecContext &&
			((ProgramExecContext)context).funcName != null) {
			methodOrFuncName = ((ProgramExecContext)context).funcName;
		}
		if(context instanceof AppClassObjExecContext &&
			((AppClassObjExecContext)context).methodOrGetterName != null) {
			methodOrFuncName = ((AppClassObjExecContext)context).methodOrGetterName;
		}

		String descriptor = context.prog.getDescriptor();
		descriptor = descriptor.substring(descriptor.indexOf(".") + 1);

		if(doEmitProgramMarkers) {
			TraceFileVerifier.enforceEmission(new PCStart(
				(execContextStack.size() == 1 ? "start" : "start-ext"),
				String.format("%02d", execContextStack.size() - 1),
				methodOrFuncName, descriptor));
			TraceFileVerifier.enforceEmission(new PCBegin(descriptor, "0", "0"));
		}

		InterpreterVisitor interpreter = new InterpreterVisitor(context, this);
		boolean normalExit = true;
		try {
			interpreter.visit(context.startNode);
		} catch(EntReturnException ere) {
			normalExit = false;
		}

		if(normalExit) {
			if(context instanceof ProgramExecContext &&
				context.scopeStack.size() > 0) {
				throw new EntVMachRuntimeException("Expected all scope ptrs in the " +
					"ProgramExecContext to be popped.");
			}

			if(context instanceof AppClassObjExecContext &&
				context.scopeStack.size() > 2) {
				throw new EntVMachRuntimeException("Expected all scope ptrs except " +
					"for the underlying object's prop and instance scopes to be popped.");
			}
		}

		if(doEmitProgramMarkers) {
			TraceFileVerifier.enforceEmission(new PCEnd(
				(execContextStack.size() == 1 ? "end" : "end-ext"),
				String.format("%02d", execContextStack.size() - 1),
				methodOrFuncName, descriptor));
		}

		execContextStack.pop();
	}

	public void runImmediately(ExecContext eCtx) {
		execContextStack.push(eCtx);
		this.runTopOfStack();
	}

	public ExecContext getThisExecContext() {
		return this.execContextStack.peek();
	}
}
