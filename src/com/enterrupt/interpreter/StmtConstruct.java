package com.enterrupt.interpreter;

import com.enterrupt.types.*;
import com.enterrupt.parser.TFlag;
import com.enterrupt.parser.Token;

public class StmtConstruct {

	public static void interpret() throws Exception {

		Token t = Interpreter.lookAheadNextToken();

		// Detect: single-line comment
		if(t.flags.contains(TFlag.COMMENT)) {
			Interpreter.parseNextToken(); // Don't need value, just move to next token.
			return;
		}

		// Detect: IF
		if(t.flags.contains(TFlag.IF)) {
			IfConstruct.interpret();
			return;
		}

		SymbolicConstruct.interpret();

		/**
		 * If next token is a semicolon, we just interpreted a function.
		 * If next token is EQUAL, we need to assign the next symbolic
		 * to this symbolic. Don't create an AssignmentConstruct to handle this,
		 * just do it here (assignment can't be done in expressions in PeopleCode).
		 */

		System.out.println("[ERROR] Encountered unexpected statement token.");
		System.exit(1);
	}
}
