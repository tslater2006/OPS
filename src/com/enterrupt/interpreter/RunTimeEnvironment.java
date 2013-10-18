package com.enterrupt.interpreter;

import java.util.HashMap;
import java.lang.reflect.*;
import java.util.Collections;
import java.util.ArrayList;
import com.enterrupt.types.*;

public class RunTimeEnvironment {

	public static BooleanPtr TRUE;
	public static BooleanPtr FALSE;

	public static HashMap<String, MemoryPtr> systemVarTable;
	public static HashMap<String, MemoryPtr> compBufferTable;
	public static HashMap<String, Method> systemFuncTable;

	public static HashMap<Integer, MemoryPtr> integerMemPool;

	public static void init() throws Exception {

		// Load static boolean literals.
		TRUE = new BooleanPtr(new Boolean(true), MFlag.READ_ONLY);
		FALSE = new BooleanPtr(new Boolean(false), MFlag.READ_ONLY);

		// Create memory pools.
		integerMemPool = new HashMap<Integer, MemoryPtr>();

		// Load system variables.
		systemVarTable = new HashMap<String, MemoryPtr>();
		systemVarTable.put("%EmployeeId", new StringPtr(MFlag.READ_ONLY));
		systemVarTable.put("%Menu", new StringPtr(MFlag.READ_ONLY));

		// Load system function references.
		systemFuncTable = new HashMap<String, Method>();
		systemFuncTable.put("None", RunTimeEnvironment.class.getMethod("None"));
		systemFuncTable.put("Hide", RunTimeEnvironment.class.getMethod("Hide"));
		systemFuncTable.put("SetSearchDialogBehavior", RunTimeEnvironment.class.getMethod("SetSearchDialogBehavior"));
		systemFuncTable.put("AllowEmplIdChg", RunTimeEnvironment.class.getMethod("AllowEmplIdChg"));

		// Initialize empty component buffer.
		compBufferTable = new HashMap<String, MemoryPtr>();

		//compBufferTable.put("LS_SS_PERS_SRCH.EMPLID", new StringPtr("test", MFlag.READ_ONLY));
	}

	public static MemoryPtr getFromMemoryPool(Integer val) {
		MemoryPtr p = integerMemPool.get(val);
		if(p != null) {
			return p;
		}
		p = new IntegerPtr(val, MFlag.READ_ONLY);
		integerMemPool.put(val, p);
		return p;
	}

	public static ArrayList<MemoryPtr> getArgsFromCallStack() {

		ArrayList<MemoryPtr> args = new ArrayList<MemoryPtr>();
		MemoryPtr p;
		while((p = Interpreter.peekAtCallStack()) != null) {
			args.add(Interpreter.popFromCallStack());
		}

		// The last argument appears at the top of the stack,
	    // so we need to reverse the argument list here before returning it.
		Collections.reverse(args);
		return args;
	}

	/**
	 * TODO: Support arbitrary argument list.
	 * TODO: The PS documentation states that 0 in a required numeric field should return false.
	 */
	public static void None() {

		ArrayList<MemoryPtr> args = getArgsFromCallStack();

		// Only supporting one argument for now.
		if(args.size() != 1) {
			System.out.println("[ERROR] Expected 1 argument for None()");
			System.exit(1);
		}

		if(args.get(0).isEmpty()) {
			Interpreter.pushToCallStack(TRUE);
		} else {
			Interpreter.pushToCallStack(FALSE);
		}
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
}
