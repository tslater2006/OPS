package com.enterrupt.runtime;

import java.util.*;
import java.lang.reflect.*;
import com.enterrupt.types.*;
import com.enterrupt.runtime.*;
import org.apache.logging.log4j.*;

public class RunTimeEnvironment {

	public static BooleanPtr TRUE;
	public static BooleanPtr FALSE;

	public static HashMap<String, MemoryPtr> systemVarTable;
	private static HashMap<String, StringPtr> compBufferTable;
	public static HashMap<String, Method> systemFuncTable;
	public static HashMap<String, Boolean> defnReservedWordTable;

	public static HashMap<Integer, MemoryPtr> integerMemPool;
	public static HashMap<String, MemoryPtr> stringMemPool;

	private static Logger log = LogManager.getLogger(RunTimeEnvironment.class.getName());

	static {

		try {
			// Load static boolean literals.
			TRUE = new BooleanPtr(new Boolean(true), MFlag.READ_ONLY);
			FALSE = new BooleanPtr(new Boolean(false), MFlag.READ_ONLY);

			// Create memory pools.
			integerMemPool = new HashMap<Integer, MemoryPtr>();
			stringMemPool = new HashMap<String, MemoryPtr>();

			// Load reserved words for definition literals.
			defnReservedWordTable = new HashMap<String, Boolean>();
			defnReservedWordTable.put("MenuName", true);

			// Load system variables.
			systemVarTable = new HashMap<String, MemoryPtr>();
			systemVarTable.put("%EmployeeId", new StringPtr(MFlag.READ_ONLY));
			systemVarTable.put("%OperatorId", new StringPtr(MFlag.READ_ONLY));
			systemVarTable.put("%Menu", new StringPtr(MFlag.READ_ONLY));

			// Load system function references.
			systemFuncTable = new HashMap<String, Method>();
			systemFuncTable.put("None", RunTimeEnvironment.class.getMethod("None"));
			systemFuncTable.put("Hide", RunTimeEnvironment.class.getMethod("Hide"));
			systemFuncTable.put("SetSearchDialogBehavior", RunTimeEnvironment.class.getMethod("SetSearchDialogBehavior"));
			systemFuncTable.put("AllowEmplIdChg", RunTimeEnvironment.class.getMethod("AllowEmplIdChg"));

			// Initialize empty component buffer.
			compBufferTable = new HashMap<String, StringPtr>();

        } catch(java.lang.NoSuchMethodException nsme) {
            log.fatal(nsme.getMessage(), nsme);
            System.exit(ExitCode.REFLECT_FAIL_RTE_STATIC_INIT.getCode());
        }
	}

	public static void setCompBufferEntry(String recordField, StringPtr ptr) {
		compBufferTable.put(recordField, ptr);
	}

	public static StringPtr getCompBufferEntry(String recordField) {
		return compBufferTable.get(recordField);
	}

	public static void setSystemVar(String var, String value) {
		((StringPtr)systemVarTable.get(var)).systemWrite(value);
	}

	public static MemoryPtr getSystemVar(String var) {
		return systemVarTable.get(var);
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

	public static MemoryPtr getFromMemoryPool(String val) {
		MemoryPtr p = stringMemPool.get(val);
		if(p != null) {
			return p;
		}
		p = new StringPtr(val, MFlag.READ_ONLY);
		stringMemPool.put(val, p);
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
			throw new EntVMachRuntimeException("Expected 1 argument for None().");
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
