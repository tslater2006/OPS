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

	public void parse(PeopleCodeProg prog) throws Exception {
		prog.appendProgText(t);
	}

	public byte getStartByte() {
		return b;
	}

	public boolean writesNonBlank() {
		return t.trim().length() > 0;
	}
}
