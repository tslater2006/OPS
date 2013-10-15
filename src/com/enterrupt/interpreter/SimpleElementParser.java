package com.enterrupt.interpreter;

import com.enterrupt.pt_objects.PeopleCodeProg;

public class SimpleElementParser extends ElementParser {

	private byte b;
	private String t;

	SimpleElementParser(byte _b, String _t, int _format) {
		b = _b;
		t = _t;
		format = _format;
	}

	SimpleElementParser(byte _b, String _t) {
		this(_b, _t, PCToken.SPACE_BEFORE_AND_AFTER);
	}

	public void parse() throws Exception {
		PCParser.prog.appendProgText(t);
	}

	public Token interpret() throws Exception {
		if(t.equals("If")) {
			Token tok = PCParser.parseNextToken();
		}
		return new Token(PCToken.IF);
	}

	public byte getStartByte() {
		return b;
	}

	public boolean writesNonBlank() {
		return t.trim().length() > 0;
	}
}
