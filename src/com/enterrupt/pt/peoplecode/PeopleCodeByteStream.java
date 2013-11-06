package com.enterrupt.pt.peoplecode;

import com.enterrupt.pt.*;

public class PeopleCodeByteStream {

	private PeopleCodeProg prog;
	private StringBuilder parsedTextBuilder;
	private int cursorPos;

	public PeopleCodeByteStream(PeopleCodeProg prog) {
		this.prog = prog;
		this.parsedTextBuilder = new StringBuilder();
	}

	public void setCursorPos(int pos) {
		this.cursorPos = pos;
	}

	public void incrementCursor() {
		this.cursorPos++;
	}

	public void decrementCursor() {
		this.cursorPos--;
	}

	public int getCursorPos() {
		return cursorPos;
	}

	public int getProgLenInBytes() {
		return this.prog.progBytes.length;
	}

	public byte readNextByte() {
		return prog.progBytes[this.cursorPos++];
	}

	public byte readAhead() {
        if(this.cursorPos >= this.prog.progBytes.length - 1) {
            return -1;
        }
        return prog.progBytes[this.cursorPos];
	}

	public Reference getMappedReference(int idx) {
		return this.prog.progRefsTable.get(idx);
	}

	public void appendParsedText(char c) {
        this.parsedTextBuilder.append(c);
	}

	public void appendParsedText(String s) {
		this.parsedTextBuilder.append(s);
	}

	public String getParsedText() {
		return this.parsedTextBuilder.toString();
	}
}
