package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;

public abstract class PTField implements PTDataType {

	public Pointer valuePtr;

	public PTField() {}

	public Pointer access(String s) {
		throw new EntDataTypeException("Need to implement access() for PTField.");
	}

	public abstract boolean equals(Object obj);
	public abstract String toString();
}
