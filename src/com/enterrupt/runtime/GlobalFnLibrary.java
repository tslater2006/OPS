package com.enterrupt.runtime;

import java.util.*;
import com.enterrupt.types.*;
import org.apache.logging.log4j.*;

public class GlobalFnLibrary {

	private static Logger log = LogManager.getLogger(GlobalFnLibrary.class.getName());

	/*************************************/
    /** Utility functions                */
    /*************************************/

    private static ArrayList<Pointer> getArgsFromCallStack() {

        ArrayList<Pointer> args = new ArrayList<Pointer>();
        Pointer p;
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
	private static boolean doesContainValue(PTDataType ptdt) {
		if(ptdt instanceof PTField
				&& ((PTField)ptdt).valuePtr.dereference() instanceof PTString) {
			return ((PTString)((PTField)ptdt).valuePtr.dereference()).value() != null;
		} else if(ptdt instanceof PTString) {
			return ((PTString)ptdt).value() != null;
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
        for(Pointer arg : getArgsFromCallStack()) {
			if(doesContainValue(arg.dereference())) {
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
        for(Pointer arg : getArgsFromCallStack()) {
			if(!doesContainValue(arg.dereference())) {
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

        ArrayList<Pointer> args = getArgsFromCallStack();

        // Expecting no arguments.
        if(args.size() != 0) {
            throw new EntVMachRuntimeException("IsModalComponent expects no arguments.");
        }

        Environment.pushToCallStack(Environment.FALSE);
    }

	public static void PT_CreateRecord() {

        ArrayList<Pointer> args = getArgsFromCallStack();

		if(args.size() != 1 || (!(args.get(0).dereference() instanceof PTString))) {
			throw new EntVMachRuntimeException("Expected single StringPtr arg to CreateRecord.");
		}

		PTRecord recObj = new PTFreeRecord(DefnCache.getRecord(
			((PTString)args.get(0).dereference()).value()));

		Environment.pushToCallStack(new StdPointer(recObj));
	}
}
