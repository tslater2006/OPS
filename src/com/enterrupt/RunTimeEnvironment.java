package com.enterrupt;

import java.util.HashMap;
import com.enterrupt.tokens.*;

public class RunTimeEnvironment {

	public static String SYSVAR_EmployeeId = "";
	public static String SYSVAR_Menu = "";

	private static HashMap<String, String> globalRefTable;

	public static void init() {
		globalRefTable = new HashMap<String, String>();
		//globalRefTable.put("LS_SS_PERS_SRCH.EMPLID", "");
	}

	public static String resolveReference(String ref) {
		return globalRefTable.get(ref);
	}

	/**
	 * TODO: Support arbitrary argument list.
	 * TODO: The PS documentation states that 0 in a required numeric field should return false.
	 * TODO: Remember that references must be resolved after retrieving from the call stack, unlike
     *       primitives.
	 */
	public static void None() {
		Token t = Interpreter.popFromCallStack();
		if(((IEvaluatable) t).isEmpty()) {
			Interpreter.pushToCallStack(new Token(TFlag.TRUE));
		} else {
			Interpreter.pushToCallStack(new Token(TFlag.FALSE));
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
