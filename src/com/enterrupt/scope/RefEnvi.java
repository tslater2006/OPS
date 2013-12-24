package com.enterrupt.scope;

import java.util.*;
import com.enterrupt.types.*;
import com.enterrupt.runtime.*;

public abstract class RefEnvi {

	private static Map<String, MemPointer> globalSymbolTable;
	private static Map<String, MemPointer> componentSymbolTable;

	static {
		globalSymbolTable = new HashMap<String, MemPointer>();
		componentSymbolTable = new HashMap<String, MemPointer>();
	}

	public abstract void declareVar(String id, MemPointer ptr);
	public abstract MemPointer resolveVar(String id);
	public abstract boolean isIdResolvable(String id);

	/******************************************/
    /** Global ref environment operations.    */
	/******************************************/

	public static void declareGlobalVar(String id, MemPointer ptr) {
		if(isGlobalVarDeclared(id)) {
			throw new EntVMachRuntimeException("Encountered attempt to re-declare " +
				" variable (" + id + ") in global ref envi.");
		}
		globalSymbolTable.put(id, ptr);
	}

	public static MemPointer resolveGlobalVar(String id) {
		return globalSymbolTable.get(id);
	}

	public static boolean isGlobalVarDeclared(String id) {
		return globalSymbolTable.containsKey(id);
	}

	/******************************************/
    /** Component ref environment operations. */
	/******************************************/

	public static void declareComponentVar(String id, MemPointer ptr) {
		if(isComponentVarDeclared(id)) {
			throw new EntVMachRuntimeException("Encountered attempt to re-declare " +
				" variable (" + id + ") in component ref envi.");
		}
		componentSymbolTable.put(id, ptr);
	}

	public static MemPointer resolveComponentVar(String id) {
		return componentSymbolTable.get(id);
	}

	public static boolean isComponentVarDeclared(String id) {
		return componentSymbolTable.containsKey(id);
	}

}
