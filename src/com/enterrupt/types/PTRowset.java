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

	public void assgmtDelegate(PTPrimitiveType src) {
		throw new EntDataTypeException("assgmtDelegate for rowset objects " +
			"is not yet supported.");
	}

	public boolean typeCheck(PTType a) {
		return (a instanceof PTRowset &&
			this.getType() == a.getType());
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
