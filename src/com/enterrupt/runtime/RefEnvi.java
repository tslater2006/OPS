package com.enterrupt.runtime;

import java.util.*;
import com.enterrupt.types.*;

public class RefEnvi {

	private String desc;

	private static Map<String, MemoryPtr> globalSymbolTable;
	private static Map<String, MemoryPtr> componentSymbolTable;

	static {
		globalSymbolTable = new HashMap<String, MemoryPtr>();
		componentSymbolTable = new HashMap<String, MemoryPtr>();
	}

	private Map<String, MemoryPtr> localSymbolTable;

	public RefEnvi(String desc) {
		this.desc = desc;
		this.localSymbolTable = new HashMap<String, MemoryPtr>();
	}

	/******************************************/
    /** Local ref environment operations.     */
	/******************************************/

	public void assignLocalVar(String id, MemoryPtr ptr) {
		this.localSymbolTable.put(id, ptr);
	}

	public MemoryPtr resolveLocalVar(String id) {
		return this.localSymbolTable.get(id);
	}

	public boolean isLocalVarDeclared(String id) {
		return this.localSymbolTable.containsKey(id);
	}

	/******************************************/
    /** Global ref environment operations.    */
	/******************************************/

	public static void assignGlobalVar(String id, MemoryPtr ptr) {
		globalSymbolTable.put(id, ptr);
	}

	public static MemoryPtr resolveGlobalVar(String id) {
		return globalSymbolTable.get(id);
	}

	public static boolean isGlobalVarDeclared(String id) {
		return globalSymbolTable.containsKey(id);
	}

	/******************************************/
    /** Component ref environment operations. */
	/******************************************/

	public static void assignComponentVar(String id, MemoryPtr ptr) {
		componentSymbolTable.put(id, ptr);
	}

	public static MemoryPtr resolveComponentVar(String id) {
		return componentSymbolTable.get(id);
	}

	public static boolean isComponentVarDeclared(String id) {
		return componentSymbolTable.containsKey(id);
	}

}
