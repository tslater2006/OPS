package com.enterrupt.interpreter;

import com.enterrupt.types.*;
import com.enterrupt.parser.TFlag;
import com.enterrupt.parser.Token;
import com.enterrupt.pt_objects.PeopleCodeTokenStream;

public class StmtConstruct {

	public static void interpret(PeopleCodeTokenStream stream) {

		Token t = stream.peekNextToken();

		// Detect: IF
		if(t.flags.contains(TFlag.IF)) {
			IfConstruct.interpret(stream);
			return;
		}

		Token resolvedToken = SymbolicConstruct.interpret(stream);
		t = stream.readNextToken();

		// Detect: SEMICOLON (must be preceded by a function call).
		if(t.flags.contains(TFlag.SEMICOLON)) {
			if(resolvedToken.flags.contains(TFlag.FUNC_REF)) {
				return;
			} else {
				throw new EntInterpretException("A token other than FUNC_REF preceded SEMICOLON.");
			}
		}

		// Detect: assignment.
		if(t.flags.contains(TFlag.EQUAL)) {
			SymbolicConstruct.interpret(stream);

			MemoryPtr srcOperand = Interpreter.popFromRuntimeStack();
			MemoryPtr destOperand = Interpreter.popFromRuntimeStack();
			MemoryPtr.copy(srcOperand, destOperand);

			t = stream.readNextToken();
			if(!t.flags.contains(TFlag.SEMICOLON)) {
				throw new EntInterpretException("Expected SEMICOLON after assignment.");
			}

			return;
		}

		throw new EntInterpretException("Encountered unexpected statement token.");
	}
}
