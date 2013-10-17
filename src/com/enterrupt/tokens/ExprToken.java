package com.enterrupt.tokens;

import com.enterrupt.Parser;
import com.enterrupt.Interpreter;

/**
 * TODO: Remember to implement order of operations.
 * Possibly use something like Dijkstra's shunting yard algorithm.
 */
public class ExprToken extends Token {

	public ExprToken() {
		super(TFlag.EXPRESSION);
	}

	public static void parse() throws Exception {

		Token t = Parser.lookAheadNextToken();

		// Detect: FN_CALL
		if(t.flags.contains(TFlag.FN_CALL)) {
			((FnCallToken) t).parse();
			return;
		}

		/**
		 * At this point, all potential options expect a Reference.
		 * TODO: This will need to be changed in the future to detect something
		 * more generic (i.e., an Evaluatable).
		 * Note: technically Evaluatable also includes an Expression, need to think about that.
		 */
		t = Parser.parseNextToken();

		// Detect: Expression $ComparisonOperator Expression
		if(Parser.lookAheadNextToken().flags.contains(TFlag.EQUAL)) {
			System.out.println("Haven't implemented comparison yet.");
			System.exit(1);
		}

		// Detect: Reference
		if(t.flags.contains(TFlag.REFERENCE)) {
			Interpreter.pushToExprStack(t);
			return;
		}

		System.out.println("[ERROR] Unexpected start of expression.");
		System.exit(1);
	}
}
