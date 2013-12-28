package com.enterrupt.types;

public interface PTDataType {
	public abstract String toString();
	public abstract boolean equals(Object obj);
	public abstract Pointer access(String s);
}
