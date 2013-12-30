package com.enterrupt.runtime;

import java.util.*;
import com.enterrupt.types.*;
import org.apache.logging.log4j.*;

public class GlobalFnLibrary {

	private static Logger log = LogManager.getLogger(GlobalFnLibrary.class.getName());

	/*************************************/
    /** Utility functions                */
    /*************************************/

    private static ArrayList<PTType> getArgsFromCallStack() {

        ArrayList<PTType> args = new ArrayList<PTType>();
        PTType p;
        while((p = Environment.peekAtCallStack()) != null) {
            args.add(Environment.popFromCallStack());
        }

        // The last argument appears at the top of the stack,
        // so we need to reverse the argument list here before returning it.
        Collections.reverse(args);
        return args;
    }

	/**
	 * This is a shared function used by logical PeopleCode functions (tests for
	 * blank values).
	 * IMPORTANT: Use this: http://it.toolbox.com/blogs/spread-knowledge/understanding-blanknull-field-values-for-using-with-all-and-none-peoplecode-functions-40672
	 * as a reference for null/blank rules with PeopleSoft data types.
	 */
	private static boolean doesContainValue(PTType p) {
		if(p instanceof PTField) {
			return doesContainValue(((PTField)p).getValue());
		} else if(p instanceof PTString) {
			return ((PTString)p).read() != null && !((PTString)p).read().equals(" ");
		} else {
			throw new EntVMachRuntimeException("Unexpected data type passed " +
				"to doesContainValue(ptdt).");
		}
	}

	/*************************************/
    /** Global system functions          */
    /*************************************/

    /**
     * Return true if none of the specified fields contain a value, return false
     * if one or more contain a value.
     */
    public static void PT_None() {
        for(PTType arg : getArgsFromCallStack()) {
			if(doesContainValue(arg)) {
	        	Environment.pushToCallStack(Environment.FALSE);
	            return;
			}
        }
        Environment.pushToCallStack(Environment.TRUE);
    }

	/**
	 * Return true if all of the specified fields contain a value, return false
	 * if one or more do not.
	 */
    public static void PT_All() {
        for(PTType arg : getArgsFromCallStack()) {
			if(!doesContainValue(arg)) {
	        	Environment.pushToCallStack(Environment.FALSE);
	            return;
			}
        }
        Environment.pushToCallStack(Environment.TRUE);
    }

    public static void PT_Hide() {
        getArgsFromCallStack();
        // Not yet implemented.
    }

    public static void PT_SetSearchDialogBehavior() {
        getArgsFromCallStack();
        // Not yet implemented.
    }

    public static void PT_AllowEmplIdChg() {
        getArgsFromCallStack();
        // Not yet implemented.
    }

    /**
     * TODO: Return true if DoModalComponent
     * has been previously called; requires more research.
     */
    public static void PT_IsModalComponent() {

        ArrayList<PTType> args = getArgsFromCallStack();

        // Expecting no arguments.
        if(args.size() != 0) {
            throw new EntVMachRuntimeException("IsModalComponent expects no arguments.");
        }

        Environment.pushToCallStack(Environment.FALSE);
    }

	public static void PT_CreateRecord() {

        ArrayList<PTType> args = getArgsFromCallStack();

		if(args.size() != 1 || (!(args.get(0) instanceof PTString))) {
			throw new EntVMachRuntimeException("Expected single StringPtr arg to CreateRecord.");
		}

		Environment.pushToCallStack(PTType.getSentinel(Type.RECORD).alloc(
			DefnCache.getRecord(((PTString)args.get(0)).read())));
	}
}
