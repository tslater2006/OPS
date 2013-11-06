package com.enterrupt.interpreter;

import java.util.Stack;
import com.enterrupt.parser.*;
import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.types.MemoryPtr;
import org.apache.logging.log4j.*;

public class Interpreter {

	private static Stack<MemoryPtr>	callStack;
	private static Stack<MemoryPtr>	runtimeStack;
	private PeopleCodeTokenStream stream;

	private static Logger log = LogManager.getLogger(Interpreter.class.getName());

	public Interpreter(PeopleCodeProg prog) {
		this.stream = new PeopleCodeTokenStream(prog);

		if(callStack == null || runtimeStack == null) {
			callStack = new Stack<MemoryPtr>();
			runtimeStack = new Stack<MemoryPtr>();
		}
	}

	public static void pushToCallStack(MemoryPtr p) {
		log.debug("Push\tCallStack\t" + (p == null ? "null" : p.flags.toString()));
		callStack.push(p);
	}

	public static MemoryPtr popFromCallStack() {
		MemoryPtr p = callStack.pop();
		log.debug("Pop\tCallStack\t\t" + (p == null ? "null" : p.flags.toString()));
		return p;
	}

	public static MemoryPtr peekAtCallStack() {
		return callStack.peek();
	}

	public static void pushToRuntimeStack(MemoryPtr p) {
		log.debug("Push\tRuntimeStack\t" + (p == null ? "null" : p.flags.toString()));
		runtimeStack.push(p);
	}

	public static MemoryPtr popFromRuntimeStack() {
		MemoryPtr p = runtimeStack.pop();
		log.debug("Pop\tRuntimeStack\t" + (p == null ? "null" : p.flags.toString()));
		return p;
	}

	public static MemoryPtr peekAtRuntimeStack() {
		return runtimeStack.peek();
	}

	public void run() {

        StmtListConstruct.interpret(stream);

        // Detect: END_OF_PROGRAM
        if(!stream.readNextToken().flags.contains(TFlag.END_OF_PROGRAM)) {
			throw new EntInterpretException("Expected END_OF_PROGRAM.");
        }
	}
}
