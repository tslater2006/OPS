package com.enterrupt.scope;

import java.util.*;
import com.enterrupt.types.*;
import com.enterrupt.runtime.*;

public class LocalRefEnvi extends RefEnvi {

	public enum Type {
		PROGRAM_LOCAL, FUNCTION_LOCAL, METHOD_LOCAL
	}

	private LocalRefEnvi.Type type;
	private Map<String, Pointer> localSymbolTable;

	public LocalRefEnvi(LocalRefEnvi.Type t) {
		this.type = t;
		this.localSymbolTable = new HashMap<String, Pointer>();
	}

	/******************************************/
    /** Local ref environment operations.     */
	/******************************************/

	public void declareVar(String id, Pointer ptr) {
		if(this.isIdResolvable(id)) {
			throw new EntVMachRuntimeException("Encountered attempt to re-declare " +
				" variable (" + id + ") in local ref envi.");
		}
		this.localSymbolTable.put(id, ptr);
	}

	public Pointer resolveVar(String id) {
		return this.localSymbolTable.get(id);
	}

	public boolean isIdResolvable(String id) {
		return this.localSymbolTable.containsKey(id);
	}
}
