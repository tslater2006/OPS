package com.enterrupt.parser;

import com.enterrupt.pt.Reference;
import com.enterrupt.pt.peoplecode.PeopleCodeByteStream;

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

		int refIdx = b2 * 256 + b1 + 1;
		Reference refObj = stream.getMappedReference(refIdx);
		if(refObj == null) {
			throw new EntParseException("No reference is mapped to index " + refIdx + " on the "
				+ "program underlying this stream.");
		}

		String ref = refObj.getValue();

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

		stream.appendParsedText(ref);

		Token t = new Token(TFlag.REFERENCE);
		t.refObj = refObj;
		return t;
	}
}
