package com.enterrupt.assembler;

import com.enterrupt.pt.peoplecode.PeopleCodeByteStream;

public class IdentifierAssembler extends StringAssembler {

	byte b;

	public IdentifierAssembler(byte _b) {
		this.b = _b;
		this.format = AFlag.SPACE_BEFORE | AFlag.SPACE_AFTER;
	}

	public byte getStartByte() {
		return this.b;
	}

	public void assemble(PeopleCodeByteStream stream) {

		stream.decrementCursor();	// current byte is 0x00, need to back up.
		stream.decrementCursor();
		stream.appendAssembledText(getString(stream));
	}
}

