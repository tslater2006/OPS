package com.enterrupt.tokens;

import java.lang.reflect.*;
import com.enterrupt.RunTimeEnvironment;
import java.util.EnumSet;
import com.enterrupt.Parser;
import com.enterrupt.Interpreter;

public class FnCallToken extends Token {

	public Method fnTarget;

	public FnCallToken(TFlag flag) {
		super(EnumSet.of(TFlag.FN_CALL, flag));
	}

	public void parse() throws Exception {

		if(!Parser.parseNextToken().flags.contains(TFlag.FN_CALL)) {
    	    System.out.println("[ERROR] Expected FN_CALL");
            System.exit(1);
        }

	    if(!Parser.parseNextToken().flags.contains(TFlag.L_PAREN)) {
    	    System.out.println("[ERROR] Expected L_PAREN");
            System.exit(1);
        }

		/**
		 * Expecting only one argument for now.
		 * TODO: Add support for indefinite arguments.
		 */
		ExprToken.parse();

        if(!Parser.parseNextToken().flags.contains(TFlag.R_PAREN)) {
            System.out.println("[ERROR] Expected R_PAREN");
	        System.exit(1);
        }

		/**
		 * Expecting only one argument for now.
		 * TODO: Add support for indefinite arguments.
		 */
		Token arg = Interpreter.popFromExprStack();
		Interpreter.pushToCallStack(new Token(TFlag.CONTEXT_BOUNDARY));
		Interpreter.pushToCallStack(arg);
		this.invoke();

		// Maximum of one argument expected / supported.
		Token ret;
		while((ret = Interpreter.popFromCallStack()) != null) {
			if(ret.flags.contains(TFlag.CONTEXT_BOUNDARY)) {
				return;
			} else {
				Interpreter.pushToExprStack(ret);
			}
		}
	}

	public void invoke() throws Exception {
        this.fnTarget.invoke(RunTimeEnvironment.class);
    }

	public boolean isNull() {
		return false;
	}
}
