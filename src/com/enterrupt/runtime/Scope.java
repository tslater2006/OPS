package com.enterrupt.runtime;

import java.util.*;
import com.enterrupt.types.*;
import com.enterrupt.runtime.*;

public class Scope {

	public enum Lvl {
		GLOBAL, COMPONENT,
		PROGRAM_LOCAL, FUNCTION_LOCAL, METHOD_LOCAL,
		APP_CLASS_OBJ_INSTANCE
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

	public PTType resolveVar(String id) {
		return this.symbolTable.get(id);
	}

	public boolean isIdResolvable(String id) {
		return this.symbolTable.containsKey(id);
	}
}