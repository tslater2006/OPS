package com.enterrupt.tokens;

import com.enterrupt.Parser;

public class ExprToken extends Token {

	public ExprToken() {
		super(TFlag.EXPRESSION);
	}

	public static Token parse() {

		ExprToken exprToken = new ExprToken();
/*		Token t = Parser.parseNextToken();

		// Detect: FUNCTION, L_PAREN, PARAM_LIST, R_PAREN
		if(t.isA(Token.FUNCTION)) {
			if(!Parser.parseNextToken().isExact(Token.L_PAREN)) {
				System.out.println("[ERROR] Expected L_PAREN");
				System.exit(1);
			}
			//ParamListToken alt = ParamListToken.parse();
			if(!Parser.parseNextToken().isExact(Token.R_PAREN)) {
				System.out.println("[ERROR] Expected R_PAREN");
				System.exit(1);
			}
		} else {
			System.out.println("[ERROR] Expected FUNCTION.");
			System.exit(1);
		}
*/
		return exprToken;
	}
}
