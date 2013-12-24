package com.enterrupt.types;

import java.util.EnumSet;

public class IntegerPtr extends PrimitivePtr<Integer> {

	private Integer i;

	public IntegerPtr() {
		super();
		this.i = 0;
	}

	public IntegerPtr(Integer initial) {
		super();
		this.i = initial;
	}

	public IntegerPtr(MFlag f) {
		super(f);
		this.i = 0;
	}

	public IntegerPtr(Integer initial, MFlag f) {
		super(f);
		this.i = initial;
	}

	public Integer read() {
		return this.i;
	}

	public void write(Integer val) {
		if(this.flags.contains(MFlag.READ_ONLY)) {
			throw new EntDataTypeException("Attempted to write to read-only location.");
		}
		this.i = val;
	}

	public void systemWrite(Integer val) {
		this.i = val;
	}

	public boolean isEmpty() {
		throw new EntDataTypeException("Have not implemented isEmpty for IntegerPtr yet.");
	}

	public String toString() {
		return i.toString();
	}
}
