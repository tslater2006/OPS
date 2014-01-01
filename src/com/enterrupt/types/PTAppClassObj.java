package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;
import com.enterrupt.types.*;
import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.runtime.*;

public class PTAppClassObj extends PTObjectType {

	private static Type staticTypeFlag = Type.APP_CLASS_OBJ;
	public AppClassPeopleCodeProg progDefn;
	public Scope instanceScope;

	protected PTAppClassObj(AppClassPeopleCodeProg prog) {
		super(staticTypeFlag);
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

	public static PTAppClassObj getSentinel(AppClassPeopleCodeProg prog) {

		// If the sentinel has already been cached, return it immediately.
		String cacheKey = getCacheKey(prog);
		if(PTType.isSentinelCached(cacheKey)) {
			return (PTAppClassObj)PTType.getCachedSentinel(cacheKey);
		}

		// Otherwise, create a new sentinel type and cache it before returning it.
		PTAppClassObj sentinelObj = new PTAppClassObj(prog);
		PTType.cacheSentinel(sentinelObj, cacheKey);
		return sentinelObj;
	}

	public PTAppClassObj alloc() {
		PTAppClassObj newObj = new PTAppClassObj(this.progDefn);
		PTType.clone(this, newObj);
		return newObj;
	}

	private static String getCacheKey(AppClassPeopleCodeProg prog) {
		StringBuilder b = new StringBuilder(staticTypeFlag.name());
		b.append(":").append(prog.getDescriptor());
		return b.toString();
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(super.toString());
		b.append(",prog=").append(this.progDefn.getDescriptor());
		return b.toString();
	}
}

