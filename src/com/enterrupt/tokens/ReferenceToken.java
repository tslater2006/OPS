package com.enterrupt.tokens;

import com.enterrupt.RunTimeEnvironment;

public class ReferenceToken extends Token implements IEvaluatable {

	private String ref;

	public ReferenceToken(String r) {
		super(TFlag.REFERENCE);
		this.ref = r;
	}

	public boolean isEmpty() {

		// Look up value in symbol table.
		/**
		 * TODO: Need to get type instead of assuming String.
	     */
		return RunTimeEnvironment.resolveReference(this.ref) == null;
	}
}
