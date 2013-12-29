package com.enterrupt.memory;

import java.util.*;
import com.enterrupt.types.*;
import com.enterrupt.runtime.*;

public abstract class RefEnvi {

	private static Map<String, Pointer> globalSymbolTable;
	private static Map<String, Pointer> componentSymbolTable;

	static {
		globalSymbolTable = new HashMap<String, Pointer>();
		componentSymbolTable = new HashMap<String, Pointer>();
	}

	public abstract void declareVar(String id, Pointer ptr);
	public abstract Pointer resolveVar(String id);
	public abstract boolean isIdResolvable(String id);

	/******************************************/
    /** Global ref environment operations.    */
	/******************************************/

	public static void declareGlobalVar(String id, Pointer ptr) {
		if(isGlobalVarDeclared(id)) {
			throw new EntVMachRuntimeException("Encountered attempt to re-declare " +
				" variable (" + id + ") in global ref envi.");
		}
		globalSymbolTable.put(id, ptr);
	}

	public static Pointer resolveGlobalVar(String id) {
		return globalSymbolTable.get(id);
	}

	public static boolean isGlobalVarDeclared(String id) {
		return globalSymbolTable.containsKey(id);
	}

	/******************************************/
    /** Component ref environment operations. */
	/******************************************/

	public static void declareComponentVar(String id, Pointer ptr) {
		if(isComponentVarDeclared(id)) {
			throw new EntVMachRuntimeException("Encountered attempt to re-declare " +
				" variable (" + id + ") in component ref envi.");
		}
		componentSymbolTable.put(id, ptr);
	}

	public static Pointer resolveComponentVar(String id) {
		return componentSymbolTable.get(id);
	}

	public static boolean isComponentVarDeclared(String id) {
		return componentSymbolTable.containsKey(id);
	}

}
