package com.enterrupt.parser;

import com.enterrupt.pt_objects.PeopleCodeByteStream;
import com.enterrupt.pt_objects.Reference;

public class ReferenceParser extends ElementParser {

	byte b;

	public ReferenceParser(byte _b) {
		this.b = _b;
		this.format = PFlags.SPACE_BEFORE_AND_AFTER;
	}

	public byte getStartByte() {
		return b;
	}

	public Token parse(PeopleCodeByteStream stream) {

		int b1 = (int) (stream.readNextByte() & 0xff);
		int b2 = (int) (stream.readNextByte() & 0xff);

		Reference refObj = stream.getMappedReference(b2 * 256 + b1 + 1);
		String ref = refObj.getValue();

		if(ref == null) {
			System.out.println("Unable to find reference number: " + b1);
			System.exit(1);
		} else {
			/**
			 * TODO: Understand what's going on here better.
			 */
			if(b == 74 && (ref.startsWith("Field.") ||
						   ref.startsWith("Record.") ||
						   ref.startsWith("Scroll."))) {
				ref = ref.substring(ref.indexOf('.') + 1);
			}
			int p1 = ref.indexOf('.');
			if(p1 > 0) {
				String rec = ref.substring(0, p1);
				if(b == (byte) 72) {
					ref = rec + ".\"" + ref.substring(p1 + 1) + "\"";
				}
			}
		}

		stream.appendParsedText(ref);

		Token t = new Token(TFlag.REFERENCE);
		t.refObj = refObj;
		return t;
	}
}
