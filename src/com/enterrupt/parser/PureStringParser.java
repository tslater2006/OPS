package com.enterrupt.parser;

import com.enterrupt.pt_objects.PeopleCodeByteStream;
import java.lang.reflect.*;
import java.util.EnumSet;

public class PureStringParser extends StringParser {

	private byte b;

	public PureStringParser(byte _b) {
		b = _b;
		format = PFlags.SPACE_BEFORE;
	}

	public PureStringParser(byte _b, int _format) {
		this(_b);
		format = _format;
	}

	public byte getStartByte() {
		return b;
	}

	public Token parse(PeopleCodeByteStream stream) {

		String str = getString(stream);
		stream.appendParsedText(str);

		Token t = new Token(TFlag.PURE_STRING);
		t.pureStrVal = str;
		return t;
	}
}
