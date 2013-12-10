package com.enterrupt.assembler;

import com.enterrupt.pt.peoplecode.PeopleCodeByteStream;

public class SimpleElementAssembler extends ElementAssembler {

	private byte b;
	private String t;

	public SimpleElementAssembler(byte _b, String _t, int _format) {
		b = _b;
		t = _t;
		format = _format;
	}

	public SimpleElementAssembler(byte _b, String _t) {
		this(_b, _t, AFlag.SPACE_BEFORE_AND_AFTER);
	}

	public byte getStartByte() {
		return b;
	}

	public void assemble(PeopleCodeByteStream stream) {
		stream.appendAssembledText(t);
	}

	public boolean writesNonBlank() {
		return t.trim().length() > 0;
	}
}
