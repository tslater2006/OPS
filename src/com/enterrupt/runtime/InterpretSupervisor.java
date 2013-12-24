package com.enterrupt.runtime;

import java.util.*;
import java.io.*;
import java.nio.charset.Charset;
import com.enterrupt.runtime.*;
import com.enterrupt.trace.*;
import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.types.*;
import com.enterrupt.scope.*;
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

		context.prog.lexAndParse();

		String methodOrFuncName = "";
		if(context instanceof ProgramExecContext &&
			((ProgramExecContext)context).funcName != null) {
			methodOrFuncName = ((ProgramExecContext)context).funcName;
		}
		if(context instanceof AppClassObjExecContext &&
			((AppClassObjExecContext)context).methodName != null) {
			methodOrFuncName = ((AppClassObjExecContext)context).methodName;
		}

		String descriptor = context.prog.getDescriptor();
		descriptor = descriptor.substring(descriptor.indexOf(".") + 1);

		TraceFileVerifier.submitEmission(new PCStart(
			(execContextStack.size() == 1 ? "start" : "start-ext"),
			String.format("%02d", execContextStack.size() - 1),
			methodOrFuncName, descriptor));
		TraceFileVerifier.submitEmission(new PCBegin(descriptor, "0", "0"));
		InterpreterVisitor interpreter = new InterpreterVisitor(context, this);
		interpreter.visit(context.startNode);

		if(context.refEnviStack.size() != 0) {
			throw new EntVMachRuntimeException("Expected all ref envi ptrs in the context " +
				" to be popped.");
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
