package com.enterrupt.scope;

import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.runtime.*;

public class AppClassObjExecContext extends ExecContext {

	public String methodName;

	public AppClassObjExecContext(AppClassPeopleCodeProg p,
				AppClassObjRefEnvi r, String m) {

		super(p);
		this.pushRefEnvi(r);

		/**
		 * Resolve method or function name to parse tree node.
		 */
		if(!p.methodEntryPoints.containsKey(this.methodName)) {
			throw new EntVMachRuntimeException("Unable to resolve method or function " +
				"name ( " + this.methodName + ") to a parse tree node for program: " +
				p.getDescriptor());
		}

		this.startNode = p.methodEntryPoints.get(this.methodName);
	}
}
