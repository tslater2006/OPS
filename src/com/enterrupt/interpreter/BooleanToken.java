package com.enterrupt.interpreter;

public class BooleanToken extends Token {

	private boolean value;

	public BooleanToken(boolean b) {
		this.value = b;
		if(b) {
			this.type = Token.TRUE;
		} else {
			this.type = Token.FALSE;
		}
	}

	public boolean getValue() {
		return value;
	}
}
