package com.enterrupt.types;

public abstract class PTObjectType extends PTType {

	protected PTObjectType(Type t) {
		super(t);
	}

	public abstract PTType dot(String s);

	/**
	 * TODO: Enable.
	 */
	//public abstract String toString();
}
