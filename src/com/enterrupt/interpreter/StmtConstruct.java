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

		Token resolvedToken = SymbolicConstruct.interpret();
		t = Interpreter.parseNextToken();

		// Detect: SEMICOLON (must be preceded by a function call).
		if(t.flags.contains(TFlag.SEMICOLON)) {
			if(resolvedToken.flags.contains(TFlag.FUNC_REF)) {
				return;
			} else {
				System.out.println("[ERROR] Parsed semicolon, but a token other than FUNCTION preceded it.");
				System.exit(1);
			}
		}

		// Detect: assignment.
		if(t.flags.contains(TFlag.EQUAL)) {
			SymbolicConstruct.interpret();

			MemoryPtr srcOperand = Interpreter.popFromRuntimeStack();
			MemoryPtr destOperand = Interpreter.popFromRuntimeStack();
			MemoryPtr.copy(srcOperand, destOperand);

			t = Interpreter.parseNextToken();
			if(!t.flags.contains(TFlag.SEMICOLON)) {
				System.out.println("[ERROR] Expected semicolon after assignment.");
				System.exit(1);
			}

			return;
		}

		System.out.println("[ERROR] Encountered unexpected statement token.");
		System.exit(1);
	}
}
