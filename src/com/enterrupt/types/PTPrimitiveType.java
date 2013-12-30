package com.enterrupt.types;

public abstract class PTPrimitiveType<T> extends PTType {

	protected PTPrimitiveType(Type t) {
		super(t);
	}

	public abstract T read();
	public abstract void write(T newValue);

	public abstract boolean equals(Object obj);

	/**
	 * TODO: Enable.
	 */
    //public abstract String toString();
}
