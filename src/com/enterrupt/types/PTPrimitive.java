package com.enterrupt.types;

public abstract class PTPrimitive<T> implements PTDataType {
	public abstract T value();
    public abstract String toString();
	public abstract boolean equals(Object obj);

	public MemPointer access(String s) {
		throw new EntDataTypeException("Encountered an illegal access() call " +
			"on a primitive data type; s=" + s);
	}
}
