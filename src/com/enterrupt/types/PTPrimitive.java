package com.enterrupt.types;

public abstract class PTPrimitive<T> implements PTDataType {
	public abstract T value();
    public abstract String toString();
	public abstract boolean equals(Object obj);
}
