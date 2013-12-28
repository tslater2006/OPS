package com.enterrupt.types;

import java.util.EnumSet;

public class ArrayPointer extends Pointer {

	public Pointer nestedTypePtr;

	/**
	 * Provided pointer should either be an ArrayPointer
	 * or a StdPointer.
	 */
	public ArrayPointer(Pointer p) {
		super(EnumSet.of(MFlag.ARRAY));
		this.nestedTypePtr = p;
	}

	public void assign(PTDataType newTarget) {
		throw new EntDataTypeException("Have not yet implemented ability to " +
			"assign to arrays.");
	}

	public String toString() {
		StringBuilder builder = new StringBuilder("AMP:");
		if(target == null) {
			builder.append("@NULL");
		} else {
			builder.append(this.target.toString());
		}
		builder.append(this.flags.toString());
		return builder.toString();
	}
}
