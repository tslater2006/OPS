package com.enterrupt.interpreter;

import java.lang.reflect.*;
import java.util.EnumSet;
import com.enterrupt.parser.TFlag;
import com.enterrupt.types.*;

public class FnCallConstruct {

	public static void interpret(Method fnPtr) throws Exception {

	    if(!Interpreter.parseNextToken().flags.contains(TFlag.L_PAREN)) {
    	    System.out.println("[ERROR] Expected L_PAREN");
            System.exit(1);
        }

		/**
		 * Expecting only one argument for now.
		 * TODO: Add support for indefinite arguments.
		 */
		ExprConstruct.interpret();

        if(!Interpreter.parseNextToken().flags.contains(TFlag.R_PAREN)) {
            System.out.println("[ERROR] Expected R_PAREN");
	        System.exit(1);
        }

		/**
		 * Expecting only one argument for now.
		 * TODO: Add support for indefinite arguments.
		 * Note: null is used to separate contexts.
		 */
		MemoryPtr arg = Interpreter.popFromRuntimeStack();
		Interpreter.pushToCallStack(null);
		Interpreter.pushToCallStack(arg);
        fnPtr.invoke(RunTimeEnvironment.class);

		MemoryPtr retPtr;
		while((retPtr = Interpreter.popFromCallStack()) != null) {
			Interpreter.pushToRuntimeStack(retPtr);
		}
	}
}
