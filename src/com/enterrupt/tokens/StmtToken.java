package com.enterrupt.tokens;

import com.enterrupt.Parser;

public class StmtToken extends Token {

	public StmtToken() {
		super(TFlag.STATEMENT);
	}

	public static void parse() throws Exception {

		Token t = Parser.lookAheadNextToken();

		// Detect: single-line comment
		if(t.flags.contains(TFlag.COMMENT)) {
			Parser.parseNextToken(); // Don't need value, just move to next token.
			return;
		}

		// [LOOKAHEAD] Detect: IF
		if(t.flags.contains(TFlag.IF)) {
			IfToken.parse();
			return;
		}

		// Detect: END_IF
	/*	if(t.isA(Token.END_IF)) {
			return new Token(Token.END_IF);
		}

		//TODO: Detec assignment

		// Detect: END_OF_PROGRAM
		if(t.isA(Token.END_OF_PROGRAM)) {
			return new Token(Token.END_OF_PROGRAM);
		}*/

		System.out.println("[ERROR] Encountered unexpected statement token.");
		System.exit(1);
	}
}
