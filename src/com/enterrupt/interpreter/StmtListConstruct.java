package com.enterrupt.interpreter;

import com.enterrupt.parser.*;
import com.enterrupt.pt.peoplecode.PeopleCodeTokenStream;

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
