package com.enterrupt.runtime;

import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.runtime.*;
import com.enterrupt.types.*;

public class AppClassDeclExecContext extends ExecContext {

	public AppClassDeclExecContext(PTAppClassObj obj) {
		super(obj.progDefn);
		this.startNode = obj.progDefn.classDeclNode;
	}
}
