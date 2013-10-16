package com.enterrupt.interpreter;

import com.enterrupt.pt_objects.PeopleCodeProg;

public class SimpleElementParser extends ElementParser {

	private byte b;
	private String t;
	private int tokenType;

	SimpleElementParser(byte _b, int _tokenType, String _t, int _format) {
		b = _b;
		tokenType = _tokenType;
		t = _t;
		format = _format;
	}

	SimpleElementParser(byte _b, int _tokenType, String _t) {
		this(_b, _tokenType, _t, PFlag.SPACE_BEFORE_AND_AFTER);
	}

	public void parse() throws Exception {
		PCParser.prog.appendProgText(t);
	}

	public Token interpret() throws Exception {
		return new Token(this.tokenType);
	}

	public byte getStartByte() {
		return b;
	}

	public boolean writesNonBlank() {
		return t.trim().length() > 0;
	}
}
