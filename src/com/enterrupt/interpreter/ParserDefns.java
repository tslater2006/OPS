package com.enterrupt.interpreter;

import com.enterrupt.pt_objects.PeopleCodeProg;

abstract class ElementParser {

	protected int format;
	public abstract void parse(PeopleCodeProg prog) throws Exception;
	public abstract byte getStartByte();

	public int getFormat() {
		return format;
	}

	public void setFormat(int format) {
		this.format = format;
	}

	public boolean writesNonBlank() {
		return true;
	}
}
