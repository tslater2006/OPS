package com.enterrupt.tokens;

import java.util.EnumSet;

public class Token {

	public EnumSet<TFlag> flags;

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
