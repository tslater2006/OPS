package com.enterrupt.interpreter;

import java.util.Stack;

import java.lang.reflect.*;
import com.enterrupt.types.*;
import com.enterrupt.parser.TFlag;
import com.enterrupt.parser.Token;

public class SymbolicConstruct {

	public static void interpret() throws Exception {

		Token t = Interpreter.parseNextToken();

		if(t.flags.contains(TFlag.REFERENCE)) {

			String refName = t.refName.trim();
			MemoryPtr ptr = null;

			// Does the reference refer to a system variable?
			if(refName.charAt(0) == '%' &&
				(ptr = RunTimeEnvironment.systemVarTable.get(refName)) != null) {

				Interpreter.pushToRuntimeStack(ptr);
				return;
			}

			// Does the reference refer to a field in the component buffer?
			if((ptr = RunTimeEnvironment.compBufferTable.get(refName)) != null) {

				Interpreter.pushToRuntimeStack(ptr);
				return;
			}

			Token l = Interpreter.lookAheadNextToken();
			Method m = null;

			// Does the reference refer to a system function?
			if(l.flags.contains(TFlag.L_PAREN) &&
				(m = RunTimeEnvironment.systemFuncTable.get(refName)) != null) {

				FnCallConstruct.interpret(m);
				return;
			}

			System.out.println("[ERROR] Unable to resolve REFERENCE token.");
			System.exit(1);
		}

		System.out.println("[ERROR] Unexpected token encountered during SymbolicConstruct interpretation.");
		System.exit(1);
	}
}
