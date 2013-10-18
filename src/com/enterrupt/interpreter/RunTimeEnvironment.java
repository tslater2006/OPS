package com.enterrupt.interpreter;

import java.util.HashMap;
import java.lang.reflect.*;
import com.enterrupt.types.*;

public class RunTimeEnvironment {

	private static BooleanPtr TRUE;
	private static BooleanPtr FALSE;

	public static HashMap<String, MemoryPtr> systemVarTable;
	public static HashMap<String, MemoryPtr> compBufferTable;
	public static HashMap<String, Method> systemFuncTable;

	private static HashMap<String, String> globalRefTable;

	public static void init() throws Exception {

		// Load static literals.
		TRUE = new BooleanPtr(new Boolean(true), MFlag.READ_ONLY);
		FALSE = new BooleanPtr(new Boolean(false), MFlag.READ_ONLY);

		/**
		 * TODO: Initialize an empty string literal and number constant pool.
		 */

		// Load system variables.
		systemVarTable = new HashMap<String, MemoryPtr>();
		systemVarTable.put("%EmployeeId", new StringPtr(MFlag.READ_ONLY));
		systemVarTable.put("%Menu", new StringPtr(MFlag.READ_ONLY));

		// Load system function references.
		systemFuncTable = new HashMap<String, Method>();
		systemFuncTable.put("None", RunTimeEnvironment.class.getMethod("None"));
		systemFuncTable.put("Hide", RunTimeEnvironment.class.getMethod("Hide"));
		systemFuncTable.put("AllowEmplIdChg", RunTimeEnvironment.class.getMethod("AllowEmplIdChg"));

		// Initialize empty component buffer.
		compBufferTable = new HashMap<String, MemoryPtr>();

		//globalRefTable.put("LS_SS_PERS_SRCH.EMPLID", "");
	}

	/**
	 * TODO: Support arbitrary argument list.
	 * TODO: The PS documentation states that 0 in a required numeric field should return false.
	 * TODO: Remember that references must be resolved after retrieving from the call stack, unlike
     *       primitives.
	 */
	public static void None() {
		MemoryPtr p = Interpreter.popFromCallStack();
		System.out.println("here");
		if(p.isEmpty()) {
			Interpreter.pushToCallStack(TRUE);
		} else {
			Interpreter.pushToCallStack(FALSE);
		}
	}

	public static void Hide() {
		// Not yet implemented.
	}

	public static void SetSearchDialogBehavior() {
		// Not yet implemented.
	}

	public static void AllowEmplIdChg() {
		// Not yet implemented.
	}
}
