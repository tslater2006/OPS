package com.enterrupt.tokens;

public class IfToken extends Token implements IBlockStartable {

	private ExprToken expression;
	private StmtListToken stmtList;

	public IfToken() {
		super(TFlag.IF);
	}

	public static IfToken parse() {

		IfToken ifToken = new IfToken();
/*
		// Detect: EXPRESSION, THEN, STMT_LIST, END_IF
		ifToken.expression = ExprToken.parse();
		if(!Parser.readNextToken().isExact(Token.THEN)) {
			System.out.println("[ERROR] Expected THEN, did not parse.");
			System.exit(1);
		}
		//TODO: Only parse stmt list if EXPRESSION evaluates to TRUE,
		// still need to find the *correct* End-If;
		//ifToken.stmtList = StmtListToken.parse();
		Token.parse(Token.END_IF);
		if(!Parser.readNextToken().isExact(Token.END_IF)) {
			System.out.println("[ERROR] Expected END_IF, did not parse.");
			System.exit(1);
		}*/
		return ifToken;
	}

	public boolean endsWith(Token t) {

		/**
		 * TODO: May want to put an identifier in this token
		 * during parsing that concretely links it with its end token
		 * for extra protection.
		 */
		return t.flags.contains(TFlag.END_IF);
	}
}
