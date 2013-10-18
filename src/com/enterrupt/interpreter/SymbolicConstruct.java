package com.enterrupt.interpreter;

import java.util.Stack;

import java.lang.reflect.*;
import com.enterrupt.types.*;
import com.enterrupt.parser.TFlag;
import com.enterrupt.parser.Token;

public class SymbolicConstruct {

	/**
	 * SymbolicConstruct resolves generic tokens (i.e., REFERENCE)
	 * to a more specific type (i.e., VAR, FUNCTION, FIELD). Calling constructs
     * (i.e., StmtConstruct) may use the additional data (provided by the returned Token) to ensure that tokens
 	 * are being interpreted as expected.
	 */
	public static Token interpret() throws Exception {

		Token t = Interpreter.parseNextToken();

		if(t.flags.contains(TFlag.REFERENCE)) {

			String refName = t.refName.trim();
			MemoryPtr ptr = null;

			// Does the reference refer to a system variable?
			if(refName.charAt(0) == '%' &&
				(ptr = RunTimeEnvironment.systemVarTable.get(refName)) != null) {

				t.flags.add(TFlag.VAR);
				Interpreter.pushToRuntimeStack(ptr);
				return t;
			}

			// Does the reference refer to a field in the component buffer?
			if((ptr = RunTimeEnvironment.compBufferTable.get(refName)) != null) {

				t.flags.add(TFlag.FIELD);
				Interpreter.pushToRuntimeStack(ptr);
				return t;
			}

			Token l = Interpreter.lookAheadNextToken();
			Method m = null;

			// Does the reference refer to a system function?
			if(l.flags.contains(TFlag.L_PAREN) &&
				(m = RunTimeEnvironment.systemFuncTable.get(refName)) != null) {

				t.flags.add(TFlag.FUNCTION);
				FnCallConstruct.interpret(m);
				return t;
			}

			System.out.println("[ERROR] Unable to resolve REFERENCE token.");
			System.exit(1);
		}

		if(t.flags.contains(TFlag.NUMBER)) {

			// Is the number an integer?
			if(t.numericVal.indexOf(".") == -1) {
				MemoryPtr ptr = RunTimeEnvironment.getFromMemoryPool(new Integer(t.numericVal));
				Interpreter.pushToRuntimeStack(ptr);
				t.flags.add(TFlag.INTEGER);
				return t;
			}

			// If not an integer, it must be a decimal.
			System.out.println("[ERROR] Detected decimal number; need to create a BigDecimal memory pool and type.");
			System.exit(1);
		}

		// Detect boolean true literal.
		if(t.flags.contains(TFlag.TRUE)) {
			Interpreter.pushToRuntimeStack(RunTimeEnvironment.TRUE);
			return t;
		}

		System.out.println("[ERROR] Unexpected token encountered during SymbolicConstruct interpretation.");
		System.exit(1);

		return null;
	}
}
