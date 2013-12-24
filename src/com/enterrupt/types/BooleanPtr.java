package com.enterrupt.types;

import java.util.EnumSet;

public class BooleanPtr extends PrimitivePtr<Boolean> {

	private Boolean b;

	public BooleanPtr() {
		super();
		this.b = false;
	}

	public BooleanPtr(Boolean initial) {
		super();
		this.b = initial;
	}

	public BooleanPtr(MFlag f) {
		super(f);
		this.b = false;
	}

	public BooleanPtr(Boolean initial, MFlag f) {
		super(f);
		this.b = initial;
	}

	public Boolean read() {
		return this.b;
	}

	public void write(Boolean val) {
		if(this.flags.contains(MFlag.READ_ONLY)) {
			throw new EntDataTypeException("Attempted to write to read-only location.");
		}
		this.b = val;
	}

	public void systemWrite(Boolean val) {
		this.b = val;
	}

	public boolean isEmpty() {
		return false;
	}

	public String toString() {
		if(this.b) { return "True"; }
		return "False";
	}
}
