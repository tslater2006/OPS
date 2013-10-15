package com.enterrupt.interpreter;

import com.enterrupt.pt_objects.PeopleCodeProg;

public class NumberParser extends ElementParser {

	byte b;
	int nBytes;

	public NumberParser(byte _b, int _nBytes) {
		b = _b;
		nBytes = _nBytes;
		format = PCToken.SPACE_BEFORE | PCToken.NO_SPACE_AFTER;
	}

	public byte getStartByte() {
		return b;
	}

	public void parse() throws Exception {

		int dValue = 0;	 // decimal position from far right going left
		String out_number = "";
		int num_bytes = nBytes - 3;
		long val = 0, fact = 1;
		PeopleCodeProg prog = PCParser.prog;

		prog.byteCursorPos++;	// skip first byte
		dValue = (int) prog.readNextByte();

		for(int i = 0; i < num_bytes; i++) {
			val += fact * (long) (prog.readNextByte() & 0xff);
			fact = fact * (long) 256;
		}

		out_number = Long.toString(val);

		if(dValue > 0 && !out_number.equals("0")) {

			while(dValue > out_number.length()) {
				out_number = "0" + out_number;
			}
			out_number = out_number.substring(0, out_number.length() - dValue) + "." +
						 out_number.substring(out_number.length() - dValue);

			if(out_number.startsWith(".")) {
				out_number = "0" + out_number;
			}
		}

		prog.appendProgText(out_number);
	}

	public void interpret() throws Exception {

	}
}
