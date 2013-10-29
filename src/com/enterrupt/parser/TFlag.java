package com.enterrupt.parser;

public enum TFlag {

	COMMENT,
	IDENTIFIER,
	NUMBER,
	TRUE,
	FALSE,
	IF,
	END_IF,
	THEN,
	END_OF_PROGRAM,
	L_PAREN,
	R_PAREN,
	SEMICOLON,
	EQUAL,
	LOCAL,
	COMMA,
	DECLARE,
	FUNCTION,
	PEOPLECODE,
	EVALUATE,
	IMPORT,
	COLON,
	COMPONENT,
	GLOBAL,
	PERIOD,
	TRY,
	WHEN,
	EXIT,
	BREAK,
	WHEN_OTHER,
	END_EVALUATE,
	AND,
	ELSE,
	NOT_EQUAL,
	AT_SIGN,
	PIPE,
	FOR,
	TO,
	END_FOR,
	MINUS,
	CREATE,
	OR,
	NOT,
	PLUS,
	ERROR,
	WARNING,
	GREATER_THAN,
	L_BRACKET,
	R_BRACKET,
	LT_OR_EQUAL,
	GT_OR_EQUAL,
	LESS_THAN,
	CATCH,
	END_TRY,
	END_FUNCTION,
	WHILE,
	END_WHILE,

	REFERENCE, PURE_STRING, EMBEDDED_STRING,

	DISCARD,							// Tells the parser to discard a token; currently used for empty identifiers.

	INTEGER,							// NUMBER tokens are given these specific flags by SymbolicConstruct

	VAR_REF, FIELD_REF, FUNC_REF, DEFN_LITERAL_REF,			// REFERENCE tokens that point to functions are given these flags
															// by SymbolicConstruct, which are then returned to calling constructs.
	END_OF_BLOCK			// signals StmtListConstruct to stop interpretation.
}
