package com.enterrupt.interpreter;

import java.lang.reflect.*;
import java.util.EnumSet;
import com.enterrupt.parser.TFlag;
import com.enterrupt.types.*;
import com.enterrupt.pt_objects.PeopleCodeTokenStream;

public class FnCallConstruct {

	public static void interpret(PeopleCodeTokenStream stream, Method fnPtr) throws Exception {

	    if(!stream.readNextToken().flags.contains(TFlag.L_PAREN)) {
    	    System.out.println("[ERROR] Expected L_PAREN");
            System.exit(1);
        }

		/**
		 * Expecting only one argument for now.
		 * TODO: Add support for indefinite arguments.
		 */
		ExprConstruct.interpret(stream);

        if(!stream.readNextToken().flags.contains(TFlag.R_PAREN)) {
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
