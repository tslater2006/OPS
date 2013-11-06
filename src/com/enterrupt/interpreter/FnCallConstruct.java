package com.enterrupt.interpreter;

import java.lang.reflect.*;
import java.util.EnumSet;
import com.enterrupt.parser.TFlag;
import com.enterrupt.types.*;
import com.enterrupt.pt_objects.PeopleCodeTokenStream;
import com.enterrupt.runtime.*;
import org.apache.logging.log4j.*;

public class FnCallConstruct {

	private static Logger log = LogManager.getLogger(FnCallConstruct.class.getName());

	public static void interpret(PeopleCodeTokenStream stream, Method fnPtr) {

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

		try {
	        fnPtr.invoke(RunTimeEnvironment.class);
		} catch(java.lang.IllegalAccessException iae) {
			log.fatal(iae.getMessage(), iae);
			System.exit(ExitCode.REFLECT_FAIL_SYS_FN_INVOCATION.getCode());
		} catch(java.lang.reflect.InvocationTargetException ite) {
			log.fatal(ite.getMessage(), ite);
			System.exit(ExitCode.REFLECT_FAIL_SYS_FN_INVOCATION.getCode());
		}

		MemoryPtr retPtr;
		while((retPtr = Interpreter.popFromCallStack()) != null) {
			Interpreter.pushToRuntimeStack(retPtr);
		}
	}
}
