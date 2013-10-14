package com.enterrupt.interpreter;

import com.enterrupt.pt_objects.PeopleCodeProg;

public class CommentParser extends ElementParser {

	private byte b;

	public CommentParser(byte _b, int _f) {
		this.b = _b;
		this.format = _f;
	}

	public byte getStartByte() {
		return b;
	}

	public void parse(PeopleCodeProg prog) throws Exception {

		// Length byte is wide ANDed and cast to integer.
		int commLen = (int) prog.readNextByte() & 0xff;
		commLen = commLen + ((int) prog.readNextByte() & 0xff) * 256;

		byte b;
		for(int i=0; i < commLen && prog.byteCursorPos < prog.progBytes.length; i++) {
			b = prog.readNextByte();
			if(b != 0) {
				if(b == (byte) 10) {
					prog.appendProgText('\n');
				} else {
					prog.appendProgText((char) b);
				}
			}
		}
	}

	public boolean writesNonBlank() {
		return true;
	}
}
