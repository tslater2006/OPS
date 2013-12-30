package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;
import com.enterrupt.types.*;
import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.runtime.*;

public class PTAppClassObject extends PTObjectType {

	public AppClassPeopleCodeProg progDefn;
	public Scope instanceScope;

	protected PTAppClassObject(AppClassPeopleCodeProg prog) {
		super(Type.APP_CLASS_OBJ);
		this.progDefn = prog;
		this.instanceScope = new Scope(Scope.Lvl.APP_CLASS_OBJ_INSTANCE);

		throw new EntDataTypeException("Need to load methods and instance-scoped " +
			"variables into this object before continuing");
	}

	public PTType dot(String s) {
		throw new EntDataTypeException("Need to support dot() on app class objs.");
	}
}

