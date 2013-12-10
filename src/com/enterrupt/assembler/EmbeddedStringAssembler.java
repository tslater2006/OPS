package com.enterrupt.assembler;

import com.enterrupt.pt.peoplecode.PeopleCodeByteStream;

public class EmbeddedStringAssembler extends PureStringAssembler {

	String pre, post;

	public EmbeddedStringAssembler(byte _b, String _pre, String _post, int _format) {
		super(_b);
		this.pre = _pre;
		this.post = _post;
		this.format = _format;
	}

	public EmbeddedStringAssembler(byte _b, String _pre, String _post) {
		this(_b, _pre, _post, AFlag.SPACE_BEFORE_AND_AFTER);
	}

	public void assemble(PeopleCodeByteStream stream) {
		stream.appendAssembledText(this.pre
			+ getString(stream).replace("\"", "\"\"") + this.post);
	}
}
