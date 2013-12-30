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

	public void assgmtDelegate(PTPrimitiveType src) {
		throw new EntDataTypeException("assgmtDelegate for app class objects " +
			"is not yet supported.");
	}

	public boolean typeCheck(PTType a) {
		return (a instanceof PTAppClassObj &&
			this.getType() == a.getType() &&
			this.progDefn == ((PTAppClassObj)a).progDefn);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(super.toString());
		b.append(",prog=").append(this.progDefn.getDescriptor());
		return b.toString();
	}
}

