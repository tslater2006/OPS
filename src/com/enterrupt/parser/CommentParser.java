package com.enterrupt.parser;

import com.enterrupt.pt_objects.PeopleCodeProg;

public class CommentParser extends ElementParser {

	private byte b;

	public CommentParser(byte _b) {
		this(_b, PFlags.NEWLINE_BEFORE_AND_AFTER);
	}

	public CommentParser(byte _b, int _f) {
		this.b = _b;
		this.format = _f;
	}

	public byte getStartByte() {
		return b;
	}

	public Token parse() throws Exception {

		PeopleCodeProg prog = Parser.prog;

		// Length byte is wide ANDed and cast to integer.
		int commLen = (int) prog.readNextByte() & 0xff;
		commLen = commLen + ((int) prog.readNextByte() & 0xff) * 256;

		if(prog.interpretFlag) {
			prog.byteCursorPos += commLen;	// fast forward through comment.
			return new Token(TFlag.COMMENT);
		}

		byte b;
		for(int i=0; i < commLen && (prog.byteCursorPos < prog.progBytes.length); i++) {
			b = prog.readNextByte();
			if(b != 0) {
				if(b == (byte) 10) {
					prog.appendProgText('\n');
				} else {
					prog.appendProgText((char) b);
				}
			}
		}

		return null;
	}

	public boolean writesNonBlank() {
		return true;
	}
}
