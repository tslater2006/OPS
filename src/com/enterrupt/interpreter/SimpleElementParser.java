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

	public void interpret() throws Exception {
		if(t.equals("If")) {
			// TODO: Peform all logic here, don't use objects for now. Check for PureStringParser, have it
			// resolve None to global function.
		}
	}

	public byte getStartByte() {
		return b;
	}

	public boolean writesNonBlank() {
		return t.trim().length() > 0;
	}
}
