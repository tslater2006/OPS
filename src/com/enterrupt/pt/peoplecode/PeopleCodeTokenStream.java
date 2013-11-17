package com.enterrupt.pt.peoplecode;

import com.enterrupt.parser.Token;
import com.enterrupt.parser.TFlag;
import org.apache.logging.log4j.*;

public class PeopleCodeTokenStream {

	private PeopleCodeProg prog;
	private int cursorPos;

	private static Logger log = LogManager.getLogger(PeopleCodeTokenStream.class.getName());

	public PeopleCodeTokenStream(PeopleCodeProg prog) {
		this.prog = prog;
	}

	public Token readNextToken() {
		log.debug(this.prog.progTokens[this.cursorPos]);
		return this.prog.progTokens[this.cursorPos++];
	}

	public Token peekNextToken() {
		return this.prog.progTokens[this.cursorPos];
	}
}
