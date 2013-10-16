package com.enterrupt.interpreter;

public class ReferenceToken extends Token {

	private String ref;

	public ReferenceToken(String r) {
		super(Token.REFERENCE);
		this.ref = r;
	}

	public boolean isNull() {

		// Look up value in symbol table.
		/**
		 * TODO: Need to get type instead of assuming String.
	     */
		return RunTimeEnvironment.resolveReference(ref) == null;
	}
}
