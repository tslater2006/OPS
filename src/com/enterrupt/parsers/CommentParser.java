package com.enterrupt.parsers;

import com.enterrupt.pt_objects.PeopleCodeProg;
import com.enterrupt.tokens.*;
import com.enterrupt.PCParser;

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

	private int getCommentLength() {

		// Length byte is wide ANDed and cast to integer.
		int commLen = (int) PCParser.prog.readNextByte() & 0xff;
		return commLen + ((int) PCParser.prog.readNextByte() & 0xff) * 256;
	}

	public void parse() throws Exception {

		PeopleCodeProg prog = PCParser.prog;
		int commLen = this.getCommentLength();

		//System.out.println("Comment length: " + commLen);
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
	}

	public Token interpret() throws Exception {

		int commLen = this.getCommentLength();
		PCParser.prog.byteCursorPos += commLen;	// fast forward through comment.
		return new Token(Token.COMMENT);
	}

	public boolean writesNonBlank() {
		return true;
	}
}
