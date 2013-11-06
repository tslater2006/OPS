package com.enterrupt.interpreter;

import com.enterrupt.pt_objects.PeopleCodeTokenStream;
import com.enterrupt.parser.TFlag;
import com.enterrupt.parser.Token;

public class StmtListConstruct {

	public static void interpret(PeopleCodeTokenStream stream) {

		while(true) {

			StmtConstruct.interpret(stream);

			/**
			 * Detect if the next token ends the current block, and thus
 			 * this stmt list.
			 */
			if(stream.peekNextToken().flags.contains(TFlag.END_OF_BLOCK)) {
				return;
			}
		}
	}
}
