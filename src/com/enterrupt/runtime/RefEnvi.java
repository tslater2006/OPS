package com.enterrupt.runtime;

import java.util.*;
import com.enterrupt.types.*;

public class RefEnvi {

	private String desc;

	private static Map<String, MemPointer> globalSymbolTable;
	private static Map<String, MemPointer> componentSymbolTable;

	static {
		globalSymbolTable = new HashMap<String, MemPointer>();
		componentSymbolTable = new HashMap<String, MemPointer>();
	}

	private Map<String, MemPointer> localSymbolTable;

	public RefEnvi(String desc) {
		this.desc = desc;
		this.localSymbolTable = new HashMap<String, MemPointer>();
	}

	/******************************************/
    /** Local ref environment operations.     */
	/******************************************/

	public void declareLocalVar(String id, MemPointer ptr) {
		if(this.isLocalVarDeclared(id)) {
			throw new EntVMachRuntimeException("Encountered attempt to re-declare " +
				" variable (" + id + ") in local ref envi.");
		}
		this.localSymbolTable.put(id, ptr);
	}

	public MemPointer resolveLocalVar(String id) {
		return this.localSymbolTable.get(id);
	}

	public boolean isLocalVarDeclared(String id) {
		return this.localSymbolTable.containsKey(id);
	}

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
