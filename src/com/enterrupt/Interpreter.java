package com.enterrupt;

import java.util.Stack;
import com.enterrupt.tokens.*;
import com.enterrupt.parsers.*;

public class Interpreter {

	private static Stack<Token> tokenStack;
	private static Stack<Token> runtimeStack;

	public static void init() {
		tokenStack = new Stack<Token>();
		runtimeStack = new Stack<Token>();
	}

	public static void pushToRuntimeStack(Token t) {
		System.out.printf("[Push] [Runtime]\t0x%02X\n", t.type);
		runtimeStack.push(t);
	}

	public static void pushToTokenStack(Token t) {
		System.out.printf("[Push] [Token]\t\t0x%02X\n", t.type);
		tokenStack.push(t);
	}

	public static Token popFromRuntimeStack() {
		Token t = runtimeStack.pop();
		System.out.printf("[Pop] [Runtime]\t0x%02X\n", t.type);
		return t;
	}

	public static Token popFromTokenStack() {
		Token t = tokenStack.pop();
		System.out.printf("[Pop] [Token]\t\t0x%02X\n", t.type);
		return t;
	}

	public static void submitToken(Token t) throws Exception {

		if(t.isExact(Token.COMMENT)) {
			return;		// don't add comments to stack.
		}

		if(t.isExact(Token.L_PAREN)) {
			if(tokenStack.peek().isA(Token.FUNCTION)) {
				pushToRuntimeStack(Parser.parseNextToken());		// push function arg to runtime stack
			}
			return; // don't push ( on stack (for now anyway)
		}

		if(t.isExact(Token.R_PAREN)) {
			if(tokenStack.peek().isA(Token.FUNCTION)) {
				FunctionToken fnToken = (FunctionToken) popFromTokenStack();
				fnToken.invoke();
				Token resultToken = popFromRuntimeStack();	// pop result from runtime stack
				pushToTokenStack(resultToken);	// now the function has been replaced by its value
			}
			return; // don't push ) on stack (for now anyway)
		}

		if(t.isExact(Token.THEN)) {
			BooleanToken exprToken = (BooleanToken) popFromTokenStack();
			if(exprToken.getValue()) {
				System.out.println("Need to evaluate body.");
			} else {
				System.out.println("Don't evaluate body.");
			}
		}

		pushToTokenStack(t);
	}
}
