package com.enterrupt.parser;

import com.enterrupt.pt_objects.PeopleCodeByteStream;

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

	public Token parse(PeopleCodeByteStream stream) {

		// Length byte is wide ANDed and cast to integer.
		int commLen = (int) stream.readNextByte() & 0xff;
		commLen = commLen + ((int) stream.readNextByte() & 0xff) * 256;

		byte b;
		for(int i=0; i < commLen && (stream.getCursorPos() < stream.getProgLenInBytes()); i++) {
			b = stream.readNextByte();
			if(b != 0) {
				if(b == (byte) 10) {
					stream.appendParsedText('\n');
				} else {
					stream.appendParsedText((char) b);
				}
			}
		}

		return new Token(TFlag.COMMENT);
	}

	public boolean writesNonBlank() {
		return true;
	}
}
