package com.enterrupt.types;

import java.util.EnumSet;

public class StringPtr extends MemoryPtr<String> {

	private String s;

	public StringPtr() {
		super(MFlag.STRING);
		this.s = null;
	}

	public StringPtr(MFlag f) {
		super(EnumSet.of(MFlag.STRING, f));
		this.s = null;
	}

	public StringPtr(String initial) {
		super(MFlag.STRING);
		this.s = initial;
	}

	public StringPtr(String initial, MFlag f) {
		super(EnumSet.of(MFlag.STRING, f));
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
