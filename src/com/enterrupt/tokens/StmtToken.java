package com.enterrupt.tokens;

import com.enterrupt.Parser;

public class StmtToken extends Token {

	public StmtToken() {
		super(TFlag.STATEMENT);
	}

	public static Token parse() {

		StmtToken stmtToken = new StmtToken();
/*		Token t = Parser.getNextToken();

		// Detect: single-line comment
		if(t.isExact(Token.COMMENT)) {
			//TODO : RETURN
		}

		// Detect: IF
		if(t.isExact(Token.IF)) {
			return IfToken.parse();
		}

		// Detect: END_IF
		if(t.isA(Token.END_IF)) {
			return new Token(Token.END_IF);
		}

		//TODO: Detec assignment

		// Detect: END_OF_PROGRAM
		if(t.isA(Token.END_OF_PROGRAM)) {
			return new Token(Token.END_OF_PROGRAM);
		}

		System.out.println("[ERROR] Encountered unexpected statement token.");
		System.exit(1);*/

		return stmtToken;
	}
}
