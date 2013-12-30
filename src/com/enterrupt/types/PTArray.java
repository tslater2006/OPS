package com.enterrupt.types;

public class PTArray extends PTObjectType {

	public int dimensions;
	public PTType baseType;

	protected PTArray(int d, PTType b) {
		super(Type.ARRAY);
		this.dimensions = d;
		this.baseType = b;
	}

	public PTType dot(String s) {
		throw new EntDataTypeException("Need to support dot() on arrays.");
	}
}

