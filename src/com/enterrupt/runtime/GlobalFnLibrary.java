package com.enterrupt.runtime;

import java.util.*;
import com.enterrupt.types.*;

public class GlobalFnLibrary {

	/*************************************/
    /** Utility functions                */
    /*************************************/

    private static ArrayList<MemoryPtr> getArgsFromCallStack() {

        ArrayList<MemoryPtr> args = new ArrayList<MemoryPtr>();
        MemoryPtr p;
        while((p = InterpretSupervisor.peekAtCallStack()) != null) {
            args.add(InterpretSupervisor.popFromCallStack());
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
     * TODO: The PS documentation states that 0 in a required numeric field should re$
     * Return true if none of the specified fields contain a value, return false
     * if one or more contain a value.
     */
    public static void None() {

        ArrayList<MemoryPtr> args = getArgsFromCallStack();

        for(MemoryPtr arg : args) {
            if(!arg.isEmpty()) {
                InterpretSupervisor.pushToCallStack(Environment.FALSE);
                return;
            }
        }

        InterpretSupervisor.pushToCallStack(Environment.TRUE);
    }

    public static void Hide() {
        getArgsFromCallStack();
        // Not yet implemented.
    }

    public static void SetSearchDialogBehavior() {
        getArgsFromCallStack();
        // Not yet implemented.
    }

    public static void AllowEmplIdChg() {
        getArgsFromCallStack();
        // Not yet implemented.
    }

    /**
     * TODO: Return true if DoModalComponent
     * has been previously called; requires more research.
     */
    public static void IsModalComponent() {

        ArrayList<MemoryPtr> args = getArgsFromCallStack();

        // Expecting no arguments.
        if(args.size() != 0) {
            throw new EntVMachRuntimeException("IsModalComponent expects no arguments.");
        }

        InterpretSupervisor.pushToCallStack(Environment.FALSE);
    }
}
