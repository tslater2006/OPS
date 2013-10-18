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
	private static Token queuedLookAheadToken;

	public static void init() {
		callStack = new Stack<MemoryPtr>();
		runtimeStack = new Stack<MemoryPtr>();
		queuedLookAheadToken = null;
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

	public static void run(PeopleCodeProg p) throws Exception {

        System.out.println("Interpreting PC...");
        Parser.reset();

		p.resetProgText();
		p.interpretFlag = true;
        p.setByteCursorPos(37);          // Program begins at byte 37.
        Parser.prog = p;

        init();

        StmtListConstruct.interpret();

        // Detect: END_OF_PROGRAM
        if(!Parser.parseNextToken().flags.contains(TFlag.END_OF_PROGRAM)) {
            System.out.println("[ERROR] Expected END_OF_PROGRAM");
            System.exit(1);
        }
	}

	public static Token parseNextToken() throws Exception {

		Token t = null;

       // If a token has been returned to the parse stream, return it immediately.
        if(queuedLookAheadToken != null) {
            t = queuedLookAheadToken;
            queuedLookAheadToken = null;
            return t;
        }

		t = Parser.parseNextToken();
		System.out.println(t.flags.toString());

		/**
		 * TODO: Resolve REFERENCE tokens here.
		 */

		return t;
	}

    // Note: Only supports 1 character of look-ahead.
    public static Token lookAheadNextToken() throws Exception {
        if(queuedLookAheadToken == null) {
            queuedLookAheadToken = parseNextToken();
        }
        return queuedLookAheadToken;
    }

    public static byte fastForwardUntil(byte startByte, byte endByte) {
        while(true) {
            byte b = Parser.prog.readNextByte();
            if(b == startByte) {
                return b;
            }
            /**
             * If we reach the end token, decrement the byte cursor to ensure that
             * when parsing resumes, it is parsed as a token.
             */
            if(b == endByte) {
                Parser.prog.byteCursorPos--;
                return b;
            }
        }
    }
}
