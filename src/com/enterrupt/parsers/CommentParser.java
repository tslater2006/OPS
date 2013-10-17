package com.enterrupt.parsers;

import com.enterrupt.pt_objects.PeopleCodeProg;
import com.enterrupt.tokens.*;
import com.enterrupt.Parser;

public class CommentParser extends ElementParser {

	private byte b;
	private int commLen;

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

	private void parseOutCommentLength() {

		// Length byte is wide ANDed and cast to integer.
		int len = (int) Parser.prog.readNextByte() & 0xff;
		this.commLen = len + ((int) Parser.prog.readNextByte() & 0xff) * 256;
	}

	public void parse() throws Exception {

		this.parseOutCommentLength();

		PeopleCodeProg prog = Parser.prog;

		//System.out.println("Comment length: " + this.commLen);
		byte b;
		for(int i=0; i < this.commLen && (prog.byteCursorPos < prog.progBytes.length); i++) {
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

		this.parseOutCommentLength();
		Parser.prog.byteCursorPos += this.commLen;	// fast forward through comment.
		return new Token(TFlag.COMMENT);
	}

	public boolean writesNonBlank() {
		return true;
	}
}
