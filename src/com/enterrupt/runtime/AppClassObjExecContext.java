package com.enterrupt.runtime;

import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.runtime.*;
import com.enterrupt.types.*;
import org.antlr.v4.runtime.tree.*;

public abstract class AppClassObjExecContext extends ExecContext {

	public String methodOrGetterName;

	public AppClassObjExecContext(PTAppClassObj obj, String m, ParseTree s) {
		super(obj.progDefn);
		this.startNode = s;
		this.methodOrGetterName = m;
		this.pushScope(obj.propertyScope);
		this.pushScope(obj.instanceScope);
	}
}
