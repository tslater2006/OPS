package com.enterrupt.tokens;

import com.enterrupt.Parser;

public class IfToken extends Token implements IBlockStartable {

	private ExprToken expression;
	private StmtListToken stmtList;

	public IfToken() {
		super(TFlag.IF);
	}

	public static void parse() throws Exception {

		IfToken ifToken = new IfToken();

		// Detect: IF, EXPRESSION, THEN, STMT_LIST, END_IF
		if(!Parser.parseNextToken().flags.contains(TFlag.IF)) {
			System.out.println("[ERROR] Expected IF, did not parse.");
			System.exit(1);
		}

		//ifToken.expression = ExprToken.parse();

		if(!Parser.parseNextToken().flags.contains(TFlag.THEN)) {
			System.out.println("[ERROR] Expected THEN, did not parse.");
			System.exit(1);
		}

		//TODO: Only parse stmt list if EXPRESSION evaluates to TRUE,
		// still need to find the *correct* End-If;
		//ifToken.stmtList = StmtListToken.parse();
		//StmtListToken.parse()

		if(!Parser.parseNextToken().flags.contains(TFlag.END_IF)) {
			System.out.println("[ERROR] Expected END_IF, did not parse.");
			System.exit(1);
		}
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
