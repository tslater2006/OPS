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

	public void parse() throws Exception {

		PCParser.prog.byteCursorPos--;	// current byte is 0x00, need to back up.
		PCParser.prog.byteCursorPos--;
		PCParser.prog.appendProgText(getString());
	}

	public Token interpret() throws Exception {
		return new Token(0);
	}
}
