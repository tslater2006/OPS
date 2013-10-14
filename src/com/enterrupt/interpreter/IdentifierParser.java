package com.enterrupt.interpreter;

import com.enterrupt.pt_objects.PeopleCodeProg;

public class IdentifierParser extends StringParser {

	byte b;

	IdentifierParser(byte _b) {
		this.b = _b;
		this.format = PCToken.SPACE_BEFORE | PCToken.SPACE_AFTER;
	}

	public byte getStartByte() {
		return this.b;
	}

	public void parse(PeopleCodeProg prog) throws Exception {

		prog.byteCursorPos--;	// current byte is 0x00, need to back up.
		prog.byteCursorPos--;
		prog.appendProgText(getString(prog));
	}
}
