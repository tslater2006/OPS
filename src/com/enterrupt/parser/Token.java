package com.enterrupt.parser;

import java.util.EnumSet;
import java.math.BigDecimal;
import com.enterrupt.pt_objects.Reference;

public class Token {

	public EnumSet<TFlag> flags;
	public Reference refObj;			// holds reference object for REFERENCE tokens.
	public String pureStrVal;			// holds value of PureString tokens.
	public String numericVal;			// holds value of NUMBER tokens.
	public String identifierName;		// holds value of IDENTIFIER tokens.

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
