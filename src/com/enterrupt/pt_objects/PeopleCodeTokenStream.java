package com.enterrupt.pt_objects;

import com.enterrupt.parser.Token;

public class PeopleCodeTokenStream {

	private PeopleCodeProg prog;
	private int cursorPos;

	public PeopleCodeTokenStream(PeopleCodeProg prog) {
		this.prog = prog;
	}

	public Token readNextToken() {
		return this.prog.progTokens[this.cursorPos++];
	}

	public Token peekNextToken() {
		return this.prog.progTokens[this.cursorPos];
	}
}
