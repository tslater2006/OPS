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

		String descriptor = context.prog.getDescriptor();
		descriptor = descriptor.substring(descriptor.indexOf(".") + 1);

		TraceFileVerifier.submitEmission(new PCStart("start", "00", "", descriptor));
		TraceFileVerifier.submitEmission(new PCBegin(descriptor, "0", "0"));
		InterpreterVisitor interpreter = new InterpreterVisitor(context, this);
		interpreter.visit(context.startNode);

		if(context.refEnviStack.size() != 0) {
			throw new EntVMachRuntimeException("Expected all ref envi ptrs in the context " +
				" to be popped.");
		}

		execContextStack.pop();
	}

	public ExecContext getThisExecContext() {
		return this.execContextStack.peek();
	}
}
