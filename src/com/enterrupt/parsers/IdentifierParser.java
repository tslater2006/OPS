package com.enterrupt.parsers;

import com.enterrupt.pt_objects.PeopleCodeProg;
import com.enterrupt.tokens.*;
import com.enterrupt.Parser;

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

		Parser.prog.byteCursorPos--;	// current byte is 0x00, need to back up.
		Parser.prog.byteCursorPos--;
		Parser.prog.appendProgText(getString());
	}

	public Token interpret() throws Exception {
		return new Token(0);
	}
}
