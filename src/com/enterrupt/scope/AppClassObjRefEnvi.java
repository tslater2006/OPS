package com.enterrupt.scope;

import java.util.*;
import com.enterrupt.types.*;
import com.enterrupt.runtime.*;

public class AppClassObjRefEnvi extends RefEnvi {

	private Map<String, Pointer> instanceSymbolTable;

	public AppClassObjRefEnvi() {
		this.instanceSymbolTable = new HashMap<String, Pointer>();
	}

	/******************************************/
    /** Instance ref environment operations.  */
	/******************************************/

	public void declareVar(String id, Pointer ptr) {
		if(this.isIdResolvable(id)) {
			throw new EntVMachRuntimeException("Encountered attempt to re-declare " +
				" variable (" + id + ") in local ref envi.");
		}
		this.instanceSymbolTable.put(id, ptr);
	}

	public Pointer resolveVar(String id) {
		return this.instanceSymbolTable.get(id);
	}

	public boolean isIdResolvable(String id) {
		return this.instanceSymbolTable.containsKey(id);
	}
}
