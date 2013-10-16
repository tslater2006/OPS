package com.enterrupt.tokens;

import com.enterrupt.Parser;

public class ExprToken extends Token {

	public ExprToken() {
		super(TFlag.EXPRESSION);
	}

	public static void parse() throws Exception {

		Token t = Parser.lookAheadNextToken();

		// Detect: FN_CALL
		if(t.flags.contains(TFlag.FN_CALL)) {
			FnCallToken.parse();
			return;
		}

		// Detect: REFERENCE

		System.out.println("[ERROR] Unexpected start of expression.");
		System.exit(1);
	}
}
