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

	DISCARD,							// Tells the parser to discard a token; currently used for empty identifiers.

	INTEGER,							// NUMBER tokens are given these specific flags by SymbolicConstruct

	VAR_REF, FIELD_REF, FUNC_REF, DEFN_LITERAL_REF,			// REFERENCE tokens that point to functions are given these flags
															// by SymbolicConstruct, which are then returned to calling constructs.
	END_OF_BLOCK			// signals StmtListConstruct to stop interpretation.
}
