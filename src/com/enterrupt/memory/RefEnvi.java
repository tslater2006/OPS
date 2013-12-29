package com.enterrupt.memory;

import java.util.*;
import com.enterrupt.types.*;
import com.enterrupt.runtime.*;

public class RefEnvi {

	public enum Type {
		GLOBAL, COMPONENT,
		PROGRAM_LOCAL, FUNCTION_LOCAL, METHOD_LOCAL,
		APP_CLASS_OBJ_INSTANCE
	}

	private RefEnvi.Type type;
	private Map<String, Pointer> symbolTable;

	public RefEnvi(RefEnvi.Type t) {
		this.type = t;
		this.symbolTable = new HashMap<String, Pointer>();
	}

	public void declareVar(String id, Pointer ptr) {
		if(this.isIdResolvable(id)) {
			throw new EntVMachRuntimeException("Encountered attempt to re-declare " +
				" variable (" + id + ") in ref envi of type " + this.type);
		}
		this.symbolTable.put(id, ptr);
	}

	public Pointer resolveVar(String id) {
		return this.symbolTable.get(id);
	}

	public boolean isIdResolvable(String id) {
		return this.symbolTable.containsKey(id);
	}
}
