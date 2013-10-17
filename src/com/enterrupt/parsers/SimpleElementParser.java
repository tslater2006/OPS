package com.enterrupt.parsers;

import com.enterrupt.pt_objects.PeopleCodeProg;
import com.enterrupt.tokens.*;
import com.enterrupt.Parser;

public class SimpleElementParser extends ElementParser {

	private byte b;
	private String t;
	private TFlag tflag;

	public SimpleElementParser(byte _b, TFlag _f, String _t, int _format) {
		b = _b;
		tflag = _f;
		t = _t;
		format = _format;
	}

	public SimpleElementParser(byte _b, TFlag _f, String _t) {
		this(_b, _f, _t, PFlags.SPACE_BEFORE_AND_AFTER);
	}

	public void parse() throws Exception {
		Parser.prog.appendProgText(t);
	}

	public Token interpret() throws Exception {
		Parser.prog.appendProgText(t);
		return new Token(tflag);
	}

	public byte getStartByte() {
		return b;
	}

	public boolean writesNonBlank() {
		return t.trim().length() > 0;
	}
}
