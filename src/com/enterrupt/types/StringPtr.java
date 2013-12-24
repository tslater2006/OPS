package com.enterrupt.types;

import java.util.EnumSet;

public class StringPtr extends PrimitivePtr<String> {

	private String s;

	public StringPtr() {
		super();
		this.s = null;
	}

	public StringPtr(String initial) {
		super();
		this.s = initial;
	}

	public StringPtr(MFlag f) {
		super(f);
		this.s = null;
	}

	public StringPtr(String initial, MFlag f) {
		super(f);
		this.s = initial;
	}

	public String read() {
		return this.s;
	}

	public void write(String val) {
		if(this.flags.contains(MFlag.READ_ONLY)) {
			throw new EntDataTypeException("Attempted to write to read-only location.");
		}
		this.s = val;
	}

	public void systemWrite(String val) {
		this.s = val;
	}

	public boolean isEmpty() {
		return this.s == null;
	}

	public String toString() {
		return "\"" + this.s + "\"";
	}
}
