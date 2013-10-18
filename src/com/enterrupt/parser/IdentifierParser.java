package com.enterrupt.parser;

import com.enterrupt.pt_objects.PeopleCodeProg;

public class IdentifierParser extends StringParser {

	byte b;

	public IdentifierParser(byte _b) {
		this.b = _b;
		this.format = PFlags.SPACE_BEFORE | PFlags.SPACE_AFTER;
	}

	public byte getStartByte() {
		return this.b;
	}

	public Token parse() throws Exception {

		Parser.prog.byteCursorPos--;	// current byte is 0x00, need to back up.
		Parser.prog.byteCursorPos--;
		Parser.prog.appendProgText(getString());

		if(Parser.prog.interpretFlag) {
			return new Token(TFlag.IDENTIFIER);
		}

		return null;
	}
}
