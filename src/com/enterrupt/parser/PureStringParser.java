package com.enterrupt.parser;

import com.enterrupt.pt_objects.PeopleCodeProg;
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

	public Token parse() throws Exception {
		String str = getString();
		Parser.prog.appendProgText(str);

		if(Parser.prog.interpretFlag) {
			Token t = new Token(TFlag.PURE_STRING);
			t.pureStrVal = str;
			System.out.println("Pure String: " + str);
			return t;
		}

		return null;
	}
}
