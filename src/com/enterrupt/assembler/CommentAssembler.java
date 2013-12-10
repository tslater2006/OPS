package com.enterrupt.assembler;

import com.enterrupt.pt.peoplecode.PeopleCodeByteStream;

public class CommentAssembler extends ElementAssembler {

	private byte b;

	public CommentAssembler(byte _b) {
		this(_b, AFlag.NEWLINE_BEFORE_AND_AFTER);
	}

	public CommentAssembler(byte _b, int _f) {
		this.b = _b;
		this.format = _f;
	}

	public byte getStartByte() {
		return b;
	}

	public void assemble(PeopleCodeByteStream stream) {

		// Length byte is wide ANDed and cast to integer.
		int commLen = (int) stream.readNextByte() & 0xff;
		commLen = commLen + ((int) stream.readNextByte() & 0xff) * 256;

		byte b;
		for(int i=0; i < commLen
				&& (stream.getCursorPos() < stream.getProgLenInBytes()); i++) {
			b = stream.readNextByte();
			if(b != 0) {
				if(b == (byte) 10) {
					stream.appendAssembledText('\n');
				} else {
					stream.appendAssembledText((char) b);
				}
			}
		}
	}

	public boolean writesNonBlank() {
		return true;
	}
}
