package com.enterrupt.tokens;

import java.lang.reflect.*;
import com.enterrupt.RunTimeEnvironment;

public class FunctionToken extends Token {

	public Method fnTarget;

	public FunctionToken(TFlag flag) {
		super(flag);
	}

	public void invoke() throws Exception {
        this.fnTarget.invoke(RunTimeEnvironment.class);
    }

	public boolean isNull() {
		return false;
	}
}
