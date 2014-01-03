package com.enterrupt.runtime;

import java.util.*;
import com.enterrupt.types.*;
import com.enterrupt.runtime.*;

public class Scope {

	public enum Lvl {
		GLOBAL, COMPONENT,
		PROGRAM_LOCAL, FUNCTION_LOCAL, METHOD_LOCAL,
		APP_CLASS_OBJ_INSTANCE, APP_CLASS_OBJ_PROPERTY
	}

	private Scope.Lvl level;
	private Map<String, PTType> symbolTable;

	public Scope(Scope.Lvl l) {
		this.level = l;
		this.symbolTable = new HashMap<String, PTType>();
	}

	public void declareVar(String id, PTType ptr) {
		if(this.isIdResolvable(id)) {
			throw new EntVMachRuntimeException("Encountered attempt to re-declare " +
				" variable (" + id + ") in scope level " + this.level);
		}
		this.symbolTable.put(id, ptr);
	}

	public void assignVar(String id, PTType newRef) {
		PTType currRef = this.symbolTable.get(id);

		if(currRef.typeCheck(newRef)) {
			this.symbolTable.put(id, newRef);
		} else {
			throw new EntVMachRuntimeException("Identifier assignment failed type " +
				"check; currRef (" + currRef.toString() + ") and newRef (" +
				newRef.toString() + ") are not type compatible.");
		}
	}

	public PTType resolveVar(String id) {
		return this.symbolTable.get(id);
	}

	public boolean isIdResolvable(String id) {
		return this.symbolTable.containsKey(id);
	}
}
