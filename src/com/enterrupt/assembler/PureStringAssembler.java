package com.enterrupt.assembler;

import java.lang.reflect.*;
import com.enterrupt.pt.peoplecode.PeopleCodeByteStream;

public class PureStringAssembler extends StringAssembler {

	private byte b;

	public PureStringAssembler(byte _b) {
		b = _b;
		format = AFlag.SPACE_BEFORE;
	}

	public PureStringAssembler(byte _b, int _format) {
		this(_b);
		format = _format;
	}

	public byte getStartByte() {
		return b;
	}

	public void assemble(PeopleCodeByteStream stream) {
		stream.appendAssembledText(getString(stream));
	}
}
