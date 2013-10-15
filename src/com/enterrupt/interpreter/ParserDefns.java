package com.enterrupt.interpreter;

import com.enterrupt.pt_objects.PeopleCodeProg;
import java.lang.StringBuilder;

abstract class ElementParser {

	protected int format;
	public abstract void parse() throws Exception;
	public abstract Token interpret() throws Exception;
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

abstract class StringParser extends ElementParser {

	String getString() {
		byte b;
		PeopleCodeProg prog = PCParser.prog;
		StringBuilder builder = new StringBuilder();

        while((b = prog.readNextByte()) != 0) {
			prog.byteCursorPos++;		//skip 0
            if(b == (byte) 10) {
	  	    	builder.append('\n');
            } else {
	            builder.append((char) b);
            }
        }
		prog.byteCursorPos++;
		return builder.toString();
	}
}
