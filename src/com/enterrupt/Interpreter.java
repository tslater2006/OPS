package com.enterrupt;

import java.util.Stack;
import com.enterrupt.tokens.*;
import com.enterrupt.parsers.*;

public class Interpreter {

	private static Stack<Token> callStack;
	private static Stack<Token> exprStack;

	public static void init() {
		callStack = new Stack<Token>();
		exprStack = new Stack<Token>();
	}

	public static void pushToCallStack(Token t) {
		System.out.println("[Push] [CallStack]\t" + t.flags.toString());
		callStack.push(t);
	}

	public static Token popFromCallStack() {
		Token t = callStack.pop();
		System.out.println("[Pop] [CallStack]\t\t" + t.flags.toString());
		return t;
	}

	public static void pushToExprStack(Token t) {
		System.out.println("[Push] [ExprStack]\t" + t.flags.toString());
		exprStack.push(t);
	}

	public static Token popFromExprStack() {
		Token t = exprStack.pop();
		System.out.println("[Pop] [ExprStack]\t\t" + t.flags.toString());
		return t;
	}
}
