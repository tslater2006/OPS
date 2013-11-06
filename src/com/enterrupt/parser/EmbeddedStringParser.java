package com.enterrupt.parser;

import com.enterrupt.pt_objects.PeopleCodeByteStream;
import java.util.EnumSet;

public class EmbeddedStringParser extends PureStringParser {

	String pre, post;

	public EmbeddedStringParser(byte _b, String _pre, String _post, int _format) {
		super(_b);
		this.pre = _pre;
		this.post = _post;
		this.format = _format;
	}

	public EmbeddedStringParser(byte _b, String _pre, String _post) {
		this(_b, _pre, _post, PFlags.SPACE_BEFORE_AND_AFTER);
	}

	public Token parse(PeopleCodeByteStream stream) {

		String str = this.pre + getString(stream).replace("\"", "\"\"") + this.post;
		stream.appendParsedText(str);

		Token t = new Token(TFlag.EMBEDDED_STRING);
		t.embeddedStrVal = str;
		return t;
	}
}
