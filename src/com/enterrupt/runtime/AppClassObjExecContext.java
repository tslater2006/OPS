package com.enterrupt.runtime;

import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.runtime.*;
import com.enterrupt.types.*;
import org.antlr.v4.runtime.tree.*;

public abstract class AppClassObjExecContext extends ExecContext {

	public String methodOrGetterName;
	public PTAppClassObj appClassObj;
	public PTType expectedReturnType;

	public AppClassObjExecContext(PTAppClassObj obj, String m, ParseTree s, PTType r) {
		super(obj.progDefn);
		this.appClassObj = obj;
		this.startNode = s;
		this.methodOrGetterName = m;
		this.expectedReturnType = r;
		this.pushScope(obj.propertyScope);
		this.pushScope(obj.instanceScope);
	}
}
