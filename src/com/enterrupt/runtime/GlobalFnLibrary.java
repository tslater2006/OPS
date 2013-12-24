package com.enterrupt.runtime;

import java.util.*;
import com.enterrupt.types.*;
import org.apache.logging.log4j.*;

public class GlobalFnLibrary {

	private static Logger log = LogManager.getLogger(GlobalFnLibrary.class.getName());

	/*************************************/
    /** Utility functions                */
    /*************************************/

    private static ArrayList<MemPointer> getArgsFromCallStack() {

        ArrayList<MemPointer> args = new ArrayList<MemPointer>();
        MemPointer p;
        while((p = Environment.peekAtCallStack()) != null) {
            args.add(Environment.popFromCallStack());
        }

        // The last argument appears at the top of the stack,
        // so we need to reverse the argument list here before returning it.
        Collections.reverse(args);
        return args;
    }

	/*************************************/
    /** Global system functions          */
    /*************************************/

    /**
     * TODO: The PS documentation states that 0 in a required numeric field should be
	 * considered as not containing a value.
     * Return true if none of the specified fields contain a value, return false
     * if one or more contain a value.
     */
    public static void PT_None() {

        ArrayList<MemPointer> args = getArgsFromCallStack();

        for(MemPointer arg : args) {
			PTDataType ptdt = arg.dereference();

			if(ptdt instanceof PTString) {
				if(((PTString)ptdt).value() != null) {
	                Environment.pushToCallStack(Environment.FALSE);
	                return;
				}
			} else {
				throw new EntVMachRuntimeException("Unexpected data type passed " +
					"to global function None().");
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

        ArrayList<MemPointer> args = getArgsFromCallStack();

        // Expecting no arguments.
        if(args.size() != 0) {
            throw new EntVMachRuntimeException("IsModalComponent expects no arguments.");
        }

        Environment.pushToCallStack(Environment.FALSE);
    }

	public static void PT_CreateRecord() {

        ArrayList<MemPointer> args = getArgsFromCallStack();

		if(args.size() != 1 || (!(args.get(0).dereference() instanceof PTString))) {
			throw new EntVMachRuntimeException("Expected single StringPtr arg to CreateRecord.");
		}

		PTRecord recObj = new PTRecord(DefnCache.getRecord(
			((PTString)args.get(0).dereference()).value()));

		Environment.pushToCallStack(new MemPointer(recObj));
	}
}
