package com.enterrupt.parser;

import java.util.*;
import java.math.BigDecimal;
import com.enterrupt.pt.Reference;

public class Token {

	public EnumSet<TFlag> flags;
	public Reference refObj;			// holds reference object for REFERENCE tokens.
	public String pureStrVal;			// holds value of PureString tokens.
	public String numericVal;			// holds value of NUMBER tokens.
	public String identifierName;		// holds value of IDENTIFIER tokens.
	public String embeddedStrVal;		// holds value of EmbeddedString tokens.

	public Token() {
		this.flags = EnumSet.noneOf(TFlag.class);
	}

	public Token(TFlag flag) {
		this.flags = EnumSet.of(flag);
	}

	public Token(EnumSet<TFlag> flagSet) {
		this.flags = EnumSet.copyOf(flagSet);
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Token ").append(this.flags);

		if(this.flags.contains(TFlag.REFERENCE)) {
			b.append(", refObj=").append(this.refObj.getValue());
		}

		if(this.flags.contains(TFlag.PURE_STRING)) {
			b.append(", pureStrVal=").append(this.pureStrVal);
		}

		if(this.flags.contains(TFlag.IDENTIFIER)) {
			b.append(", numericVal=").append(this.numericVal);
		}

		if(this.flags.contains(TFlag.EMBEDDED_STRING)) {
			b.append(", embeddedStrVal=").append(this.embeddedStrVal);
		}
		return b.toString();
	}
}
