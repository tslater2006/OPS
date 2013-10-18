package com.enterrupt.parser;

import java.util.EnumSet;
import java.math.BigDecimal;

public class Token {

	public EnumSet<TFlag> flags;
	public String refName;				// holds reference name for REFERENCE tokens.
	public String numericVal;			// holds value of NUMBER tokens.

	public Token() {
		this.flags = EnumSet.noneOf(TFlag.class);
	}

	public Token(TFlag flag) {
		this.flags = EnumSet.of(flag);
	}

	public Token(EnumSet<TFlag> flagSet) {
		this.flags = EnumSet.copyOf(flagSet);
	}
}
