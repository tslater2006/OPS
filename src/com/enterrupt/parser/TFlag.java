package com.enterrupt.parser;

public enum TFlag {

	COMMENT,
	IDENTIFIER,
	NUMBER,
	TRUE,
	IF,
	END_IF,
	THEN,
	END_OF_PROGRAM,
	L_PAREN,
	R_PAREN,
	SEMICOLON,
	EQUAL,
	REFERENCE,

	END_OF_BLOCK				// signals StmtListConstruct to stop interpretation.
}
