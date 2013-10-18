package com.enterrupt.interpreter;

import com.enterrupt.types.*;
import com.enterrupt.parser.TFlag;
import com.enterrupt.parser.Token;

public class StmtListConstruct {

	public static void interpret() throws Exception {

		while(true) {

			StmtConstruct.interpret();

			/**
			 * Detect if the next token ends the current block, and thus
 			 * this stmt list.
			 */
			Token t = Interpreter.lookAheadNextToken();
			if(t.flags.contains(TFlag.END_OF_BLOCK)) {
				return;
			}
		}
	}
}
