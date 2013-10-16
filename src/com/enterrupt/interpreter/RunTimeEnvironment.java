package com.enterrupt.interpreter;

import java.util.HashMap;

public class RunTimeEnvironment {

	public static String SYSVAR_EmployeeId = "";
	public static String SYSVAR_Menu = "";

	private static HashMap<String, Boolean> globalRefTable;

	public static void init() {
		globalRefTable = new HashMap<String, Boolean>();
	}

	/**
	 * TODO: In the future, this should point to an object
     * that can be modified/updated by the interpreter.
	 */
	public static Boolean resolveReference(String ref) {
		return globalRefTable.get(ref);
	}

	/**
	 * TODO: Support arbitrary argument list.
	 * TODO: The PS documentation states that 0 in a required numeric field should return false.
	 */
	public static void None() {
		Token t = Interpreter.popFromRuntimeStack();
		if(t.isNull()) {
			Interpreter.pushToRuntimeStack(new BooleanToken(true));
		} else {
			Interpreter.pushToRuntimeStack(new BooleanToken(false));
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
