package com.enterrupt.runtime;

import java.util.Stack;
import java.io.*;
import java.nio.charset.Charset;
import com.enterrupt.runtime.*;
import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.types.MemoryPtr;
import org.apache.logging.log4j.*;
import com.enterrupt.antlr4.*;

public class InterpretSupervisor {

	private PeopleCodeProg rootProg;
	private Stack<ExecutionContext> contextStack;

	private static Logger log = LogManager.getLogger(InterpretSupervisor.class.getName());

	public InterpretSupervisor(PeopleCodeProg prog) {
		this.rootProg = prog;
		this.contextStack = new Stack<ExecutionContext>();
	}

	public void run() {

		ExecutionContext initialCtx = new ProgramExecContext(this.rootProg);
		this.contextStack.push(initialCtx);
		this.runTopOfStack();

		if(this.contextStack.size() != 0) {
			throw new EntVMachRuntimeException("Expected context stack to be empty.");
		}

		if(Environment.getCallStackSize() != 0) {
			throw new EntVMachRuntimeException("Expected call stack to be empty.");
		}
	}

	private void runTopOfStack() {

		this.rootProg.lexAndParse();

		String descriptor = this.rootProg.getDescriptor();
		descriptor = descriptor.substring(descriptor.indexOf(".") + 1);

  		log.info("  >>> start     Nest=00  {}", descriptor);
		log.info(">>>>> Begin {} level 0 row 0", descriptor);
		InterpreterVisitor interpreter = new InterpreterVisitor(
			this.rootProg.tokenStream, this);
		interpreter.visit(this.rootProg.parseTree);

		contextStack.pop();
	}
}

class ExecutionContext {

	public ExecutionContext() {}
}

class ProgramExecContext extends ExecutionContext {

	private PeopleCodeProg prog;

	public ProgramExecContext(PeopleCodeProg p) {
		this.prog = p;
	}
}

class FunctionExecContext extends ExecutionContext {}
class GlobalExecContext extends ExecutionContext {}
class ComponentExecContext extends ExecutionContext {}
