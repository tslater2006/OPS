package com.enterrupt.parsers;

import com.enterrupt.pt_objects.PeopleCodeProg;
import com.enterrupt.tokens.*;
import com.enterrupt.PCParser;

public class IdentifierParser extends StringParser {

	byte b;

	public IdentifierParser(byte _b) {
		this.b = _b;
		this.format = PFlags.SPACE_BEFORE | PFlags.SPACE_AFTER;
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
