package com.enterrupt.interpreter;

import com.enterrupt.types.*;
import com.enterrupt.parser.TFlag;
import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.runtime.*;

public class ExprConstruct {

	public static void interpret(PeopleCodeTokenStream stream) {

		SymbolicConstruct.interpret(stream);

		// Detect: SymbolicConstruct $ComparisonOperator SymbolicConstruct
		if(stream.peekNextToken().flags.contains(TFlag.EQUAL)) {
			/**
			 * TODO: Interpret another SymbolicConstruct, pop 2 values from runtime stack,
			 * compare them, and push the result.
		     */
			stream.readNextToken();
			SymbolicConstruct.interpret(stream);

			MemoryPtr p2 = Interpreter.popFromRuntimeStack();
			MemoryPtr p1 = Interpreter.popFromRuntimeStack();

			if(MemoryPtr.isEqual(p1, p2)) {
				Interpreter.pushToRuntimeStack(RunTimeEnvironment.TRUE);
			} else {
				Interpreter.pushToRuntimeStack(RunTimeEnvironment.FALSE);
			}
		}
	}
}
