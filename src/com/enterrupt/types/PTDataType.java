package com.enterrupt.types;

import com.enterrupt.memory.*;

public interface PTDataType {
	public abstract String toString();
	public abstract boolean equals(Object obj);
	public abstract Pointer access(String s);
}
