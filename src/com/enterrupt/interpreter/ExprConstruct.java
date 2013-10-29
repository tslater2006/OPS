package com.enterrupt.interpreter;

import com.enterrupt.types.*;
import com.enterrupt.parser.TFlag;
import com.enterrupt.pt_objects.PeopleCodeTokenStream;

/**
 * TODO: Remember to implement order of operations.
 * Possibly use something like Dijkstra's shunting yard algorithm.
 */
public class ExprConstruct {

	public static void interpret(PeopleCodeTokenStream stream) throws Exception {

		/**
		 * TODO: Immediately interpret a SymbolicConstruct.
		 * Then look ahead to see if any relational operators or mathematical
		 * operators exist. If not, return.
		 */

		/**
		 * TODO: Technically an Expression could be the next token we need to parse.
		 * Can we detect that just by checking for parentheses before interpreting
		 * a SymbolicConstruct?
		 */

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
