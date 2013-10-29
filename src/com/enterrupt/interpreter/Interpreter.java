package com.enterrupt.interpreter;

import java.util.Stack;
import com.enterrupt.parser.Token;
import com.enterrupt.parser.TFlag;
import com.enterrupt.parser.Parser;
import com.enterrupt.pt_objects.PeopleCodeProg;
import com.enterrupt.types.MemoryPtr;

public class Interpreter {

	private static Stack<MemoryPtr>	callStack;
	private static Stack<MemoryPtr>	runtimeStack;
	private PeopleCodeProg prog;

	public Interpreter(PeopleCodeProg prog) {
		this.prog = prog;

		if(callStack == null || runtimeStack == null) {
			callStack = new Stack<MemoryPtr>();
			runtimeStack = new Stack<MemoryPtr>();
		}
	}

	public static void pushToCallStack(MemoryPtr p) {
		System.out.println("[Push] [CallStack]\t" + (p == null ? "null" : p.flags.toString()));
		callStack.push(p);
	}

	public static MemoryPtr popFromCallStack() {
		MemoryPtr p = callStack.pop();
		System.out.println("[Pop] [CallStack]\t\t" + (p == null ? "null" : p.flags.toString()));
		return p;
	}

	public static MemoryPtr peekAtCallStack() {
		return callStack.peek();
	}

	public static void pushToRuntimeStack(MemoryPtr p) {
		System.out.println("[Push] [RuntimeStack]\t" + (p == null ? "null" : p.flags.toString()));
		runtimeStack.push(p);
	}

	public static MemoryPtr popFromRuntimeStack() {
		MemoryPtr p = runtimeStack.pop();
		System.out.println("[Pop] [RuntimeStack]\t\t" + (p == null ? "null" : p.flags.toString()));
		return p;
	}

	public static MemoryPtr peekAtRuntimeStack() {
		return runtimeStack.peek();
	}

	public void run() throws Exception {

        StmtListConstruct.interpret(prog);

        // Detect: END_OF_PROGRAM
        if(!prog.readNextToken().flags.contains(TFlag.END_OF_PROGRAM)) {
            System.out.println("[ERROR] Expected END_OF_PROGRAM");
            System.exit(1);
        }
	}
}
