package com.enterrupt.types;

public abstract class PTObjectType extends PTType {

	protected PTObjectType(Type t) {
		super(t);
	}

	public abstract PTType dot(String s);
	public abstract void assgmtDelegate(PTPrimitiveType src);
}
