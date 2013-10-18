package com.enterrupt.interpreter;

import com.enterrupt.parser.TFlag;

/**
 * TODO: Remember to implement order of operations.
 * Possibly use something like Dijkstra's shunting yard algorithm.
 */
public class ExprConstruct {

	public static void interpret() throws Exception {

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

		SymbolicConstruct.interpret();

		// Detect: SymbolicConstruct $ComparisonOperator SymbolicConstruct
		if(Interpreter.lookAheadNextToken().flags.contains(TFlag.EQUAL)) {
			/**
			 * TODO: Interpret another SymbolicConstruct, pop 2 values from runtime stack,
			 * compare them, and push the result.
		     */
			System.out.println("Haven't implemented comparison yet.");
			System.exit(1);
		}
	}
}
