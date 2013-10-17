package com.enterrupt.tokens;

public enum TFlag {

	SYSTEM,										// combined with FUNCTION, VARIABLE
	COMMENT,
	VARIABLE, FN_CALL, REFERENCE, IDENTIFIER, NUMBER, TRUE, FALSE,		// these all evaluate to some value

	IF,
	END_IF,
	THEN,
	END_OF_PROGRAM,

	START_OF_BLOCK,				// combined with IF
	END_OF_BLOCK,				// combined with END_IF, END_OF_PROGRAM

	L_PAREN, R_PAREN, SEMICOLON, EQUAL,

	BOOLEAN, EXPRESSION, STATEMENT, STMT_LIST,

	CONTEXT_BOUNDARY		// delimits context boundaries on call stack.
}
