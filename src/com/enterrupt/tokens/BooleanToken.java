package com.enterrupt.tokens;

import com.enterrupt.RunTimeEnvironment;
import java.util.EnumSet;

public class BooleanToken extends Token implements IEvaluatable {

	private String ref;

	public BooleanToken(boolean b) {
		super(TFlag.BOOLEAN);
		if(b) {
			this.flags.add(TFlag.TRUE);
		} else {
			this.flags.add(TFlag.FALSE);
		}
	}

	public boolean getValue() {
		if(this.flags.equals(EnumSet.of(TFlag.BOOLEAN, TFlag.TRUE))) {
			return true;
		} else if(this.flags.equals(EnumSet.of(TFlag.BOOLEAN, TFlag.FALSE))) {
			return false;
		} else {
			System.out.println("[ERROR] Boolean token evaluates to neither true nor false.");
			System.exit(1);
		}
		return false;
	}

	public boolean isEmpty() {
		return false;
	}
}
