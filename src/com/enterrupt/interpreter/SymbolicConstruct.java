package com.enterrupt.interpreter;

import java.util.Stack;

import java.lang.reflect.*;
import com.enterrupt.types.*;
import com.enterrupt.parser.TFlag;
import com.enterrupt.parser.Token;
import com.enterrupt.pt_objects.PeopleCodeTokenStream;

public class SymbolicConstruct {

	/**
	 * SymbolicConstruct resolves generic tokens (i.e., REFERENCE, PURE_STRING, NUMBER)
	 * to a more specific type (i.e., VAR_REF, FUNC_REF, FIELD_REF). Calling constructs
     * (i.e., StmtConstruct) may use the additional data (provided by the returned Token) to ensure that tokens
 	 * are being interpreted as expected.
	 */
	public static Token interpret(PeopleCodeTokenStream stream) {

		Token t = stream.readNextToken();
		MemoryPtr ptr = null;

		if(t.flags.contains(TFlag.REFERENCE)) {

			String refName = t.refObj.getValue();

			// Does the reference refer to a field in the component buffer?
			if((ptr = RunTimeEnvironment.compBufferTable.get(refName)) != null) {

				t.flags.add(TFlag.FIELD_REF);
				Interpreter.pushToRuntimeStack(ptr);
				return t;
			}

			// Does the reference refer to a definition literal (i.e., "MenuName.____")?
			String[] parts = refName.split("\\.");		// split accepts regex, we want to split on '.' char.
			if(parts.length == 2
				&& parts[1].length() > 0
				&& (RunTimeEnvironment.defnReservedWordTable.get(parts[0]) != null)) {

				ptr = RunTimeEnvironment.getFromMemoryPool(parts[1]);
				Interpreter.pushToRuntimeStack(ptr);
				t.flags.add(TFlag.DEFN_LITERAL_REF);
				return t;
			}

			System.out.println("[ERROR] Unable to resolve REFERENCE token.");
			System.exit(1);
		}

		if(t.flags.contains(TFlag.PURE_STRING)) {

			// Does the pure string refer to a system variable?
			if(t.pureStrVal.charAt(0) == '%' &&
				(ptr = RunTimeEnvironment.systemVarTable.get(t.pureStrVal)) != null) {

				t.flags.add(TFlag.VAR_REF);
				Interpreter.pushToRuntimeStack(ptr);
				return t;
			}

			Token l = stream.peekNextToken();
			Method m = null;

			// Does the pure string refer to a system function?
			if(l.flags.contains(TFlag.L_PAREN) &&
				(m = RunTimeEnvironment.systemFuncTable.get(t.pureStrVal)) != null) {

				t.flags.add(TFlag.FUNC_REF);
				FnCallConstruct.interpret(stream, m);
				return t;
			}

			System.out.println("[ERROR] Unable to resolve PURE_STRING token.");
			System.exit(1);
		}

		if(t.flags.contains(TFlag.NUMBER)) {

			// Is the number an integer?
			if(t.numericVal.indexOf(".") == -1) {
				ptr = RunTimeEnvironment.getFromMemoryPool(new Integer(t.numericVal));
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

		System.out.println(t.flags);
		System.out.println("[ERROR] Unexpected token encountered during SymbolicConstruct interpretation.");
		System.exit(1);

		return null;
	}
}
