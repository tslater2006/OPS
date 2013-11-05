package com.enterrupt.parser;

import com.enterrupt.pt_objects.PeopleCodeByteStream;

public class IdentifierParser extends StringParser {

	byte b;

	public IdentifierParser(byte _b) {
		this.b = _b;
		this.format = PFlags.SPACE_BEFORE | PFlags.SPACE_AFTER;
	}

	public byte getStartByte() {
		return this.b;
	}

	public Token parse(PeopleCodeByteStream stream) throws Exception {

		stream.decrementCursor();	// current byte is 0x00, need to back up.
		stream.decrementCursor();
		String str = getString(stream);

		stream.appendParsedText(str);

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

