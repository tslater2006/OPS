package com.enterrupt.pt.peoplecode;

import com.enterrupt.parser.Token;
import com.enterrupt.parser.TFlag;

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
