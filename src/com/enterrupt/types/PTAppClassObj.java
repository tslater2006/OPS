package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;
import com.enterrupt.types.*;
import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.runtime.*;

public class PTAppClassObj extends PTObjectType {

	public AppClassPeopleCodeProg progDefn;
	public Scope instanceScope;

	protected PTAppClassObj(AppClassPeopleCodeProg prog) {
		super(Type.APP_CLASS_OBJ);
		this.progDefn = prog;
		this.instanceScope = new Scope(Scope.Lvl.APP_CLASS_OBJ_INSTANCE);

		/**
		 * TODO: Load methods and instance variables here from prog defn.
	 	 */
	}

	public PTType dot(String s) {
		throw new EntDataTypeException("Need to support dot() on app class objs.");
	}
}

