package com.enterrupt.tokens;

import java.lang.reflect.*;
import com.enterrupt.RunTimeEnvironment;
import java.util.EnumSet;
import com.enterrupt.Parser;

public class FnCallToken extends Token {

	public Method fnTarget;

	public FnCallToken(TFlag flag) {
		super(EnumSet.of(TFlag.FN_CALL, flag));
	}

	public static void parse() throws Exception {

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
	}

	public void invoke() throws Exception {
        this.fnTarget.invoke(RunTimeEnvironment.class);
    }

	public boolean isNull() {
		return false;
	}
}
