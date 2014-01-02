package com.enterrupt.runtime;

import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.runtime.*;
import com.enterrupt.types.*;

public class AppClassObjExecContext extends ExecContext {

	public String methodName;

	public AppClassObjExecContext(PTAppClassObj obj, String m) {
		super(obj.progDefn);
		this.methodName = m;
		this.pushScope(obj.instanceScope);

		/**
		 * Resolve method or function name to parse tree node.
		 */
		if(!obj.progDefn.methodImplStartNodes.containsKey(this.methodName)) {
			throw new EntVMachRuntimeException("Unable to resolve method or function " +
				"name (" + this.methodName + ") to a parse tree node for program: " +
				obj.progDefn.getDescriptor());
		}

		this.startNode = obj.progDefn.methodImplStartNodes.get(this.methodName);
	}
}
