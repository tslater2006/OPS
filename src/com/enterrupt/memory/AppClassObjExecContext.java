package com.enterrupt.memory;

import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.runtime.*;
import com.enterrupt.types.*;

public class AppClassObjExecContext extends ExecContext {

	public String methodName;

	public AppClassObjExecContext(PTAppClassObject obj, String m) {
		super(obj.progDefn);
		this.methodName = m;
		this.pushRefEnvi(obj.persistentRefEnvi);

		/**
		 * Resolve method or function name to parse tree node.
		 */
		if(!obj.progDefn.methodEntryPoints.containsKey(this.methodName)) {
			throw new EntVMachRuntimeException("Unable to resolve method or function " +
				"name (" + this.methodName + ") to a parse tree node for program: " +
				obj.progDefn.getDescriptor());
		}

		this.startNode = obj.progDefn.methodEntryPoints.get(this.methodName);
	}
}
