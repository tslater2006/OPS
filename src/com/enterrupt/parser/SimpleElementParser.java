package com.enterrupt.parser;

import java.util.EnumSet;
import com.enterrupt.pt.peoplecode.PeopleCodeByteStream;

public class SimpleElementParser extends ElementParser {

	private byte b;
	private String t;
	private EnumSet<TFlag> tflags;

	public SimpleElementParser(byte _b, EnumSet<TFlag> _e, String _t, int _format) {
		b = _b;
		tflags = _e;
		t = _t;
		format = _format;
	}

	public SimpleElementParser(byte _b, EnumSet<TFlag> _e, String _t) {
		this(_b, _e, _t, PFlags.SPACE_BEFORE_AND_AFTER);
	}

	public byte getStartByte() {
		return b;
	}

	public Token parse(PeopleCodeByteStream stream) {

		stream.appendParsedText(t);
		return new Token(this.tflags);
	}

	public boolean writesNonBlank() {
		return t.trim().length() > 0;
	}
}
