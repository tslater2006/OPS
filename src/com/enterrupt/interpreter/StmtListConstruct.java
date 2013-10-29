package com.enterrupt.interpreter;

import com.enterrupt.pt_objects.PeopleCodeProg;
import com.enterrupt.parser.TFlag;
import com.enterrupt.parser.Token;

public class StmtListConstruct {

	public static void interpret(PeopleCodeProg prog) throws Exception {

		while(true) {

			StmtConstruct.interpret(prog);

			/**
			 * Detect if the next token ends the current block, and thus
 			 * this stmt list.
			 */
			if(prog.peekNextToken().flags.contains(TFlag.END_OF_BLOCK)) {
				return;
			}
		}
	}
}
