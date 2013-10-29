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

	public Token parse(PeopleCodeProg prog) throws Exception {

		prog.byteCursorPos--;	// current byte is 0x00, need to back up.
		prog.byteCursorPos--;
		String str = getString(prog);

		prog.appendProgText(str);

		Token t = new Token(TFlag.IDENTIFIER);
		t.identifierName = str;

		// Will cause parseNextToken to discard this token and replace it
		// with the next one in the stream.
		if(str.length() == 0) {
			t.flags.add(TFlag.DISCARD);
		}

		return t;
	}
}

