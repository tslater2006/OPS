package com.enterrupt.memory;

import java.util.EnumSet;
import com.enterrupt.types.*;

public class ArrayPointer extends Pointer {

	public int dimensions;
	public Pointer baseTypePtr;

	public ArrayPointer(Pointer p) {
		super(EnumSet.of(MFlag.ARRAY));
		this.dimensions = 1;
		this.baseTypePtr = p;
	}

	public void assign(PTDataType newTarget) {
		throw new EntDataTypeException("Have not yet implemented ability to " +
			"assign to arrays.");
	}

	public String toString() {
		StringBuilder builder = new StringBuilder("Array(dim=");
		builder.append(this.dimensions).append(",");
		builder.append("baseTypePtr=").append(this.baseTypePtr.toString());
		builder.append(")").append(this.flags.toString());
		return builder.toString();
	}
}
