package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;

public class PTRowset extends PTObjectType {

	protected PTRowset() {
		super(Type.ROWSET);
	}

	public PTType dot(String s) {
		throw new EntDataTypeException("Need to implement dot() for PTRowset.");
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
