package com.enterrupt.types;

import java.util.EnumSet;

public class CBufferPointer extends Pointer {

	public CBufferPointer(PTRecord rec) {
		super(EnumSet.of(MFlag.CBUFFER, MFlag.READ_ONLY, MFlag.RECORD));
		this.target = rec;
	}

	public CBufferPointer(PTField fld) {
		super(EnumSet.of(MFlag.CBUFFER, MFlag.READ_ONLY, MFlag.FIELD));
		this.target = fld;
	}

	public void assign(PTDataType operand) {
		throw new EntDataTypeException("Encountered illegal attempt to assign " +
			"new reference to a CBufferPointer; component buffer pointers " +
			"can not be modified.");
	}

	public String toString() {
		StringBuilder builder = new StringBuilder("CBMP:");
		if(target == null) {
			builder.append("@NULL");
		} else {
			builder.append(this.target.toString());
		}
		builder.append(this.flags.toString());
		return builder.toString();
	}
}
