package com.enterrupt.interpreter;

import com.enterrupt.pt_objects.PeopleCodeProg;

public class PureStringParser extends StringParser {

	private byte b;

	PureStringParser(byte _b) {
		b = _b;
		format = PCToken.SPACE_BEFORE;
	}

	PureStringParser(byte _b, int _format) {
		this(_b);
		format = _format;
	}

	public byte getStartByte() {
		return b;
	}

	public void parse(PeopleCodeProg prog) throws Exception {
		prog.appendProgText(getString(prog));
	}
}
