package com.enterrupt.types;

import com.enterrupt.runtime.*;

public abstract class PTObjectType extends PTType {

	protected PTObjectType(Type t) {
		super(t);
	}

	public abstract PTType dotProperty(String s);
	public abstract Callable dotMethod(String s);
	public abstract PTPrimitiveType castTo(PTPrimitiveType t);
	public abstract boolean typeCheck(PTType a);
}
