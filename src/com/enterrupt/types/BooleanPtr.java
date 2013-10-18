package com.enterrupt.types;

import java.util.EnumSet;

public class BooleanPtr extends MemoryPtr<Boolean> {

	private Boolean b;

	public BooleanPtr() {
		super(MFlag.BOOLEAN);
		this.b = false;
	}

	public BooleanPtr(MFlag f) {
		super(EnumSet.of(MFlag.BOOLEAN, f));
		this.b = false;
	}

	public BooleanPtr(Boolean initial) {
		super(MFlag.BOOLEAN);
		this.b = initial;
	}

	public BooleanPtr(Boolean initial, MFlag f) {
		super(EnumSet.of(MFlag.BOOLEAN, f));
		this.b = initial;
	}

	public Boolean read() {
		return this.b;
	}

	public void write(Boolean val) {
		if(this.flags.contains(MFlag.READ_ONLY)) {
			System.out.println("[ERROR] Attempted to write to read-only location.");
			System.exit(1);
		}
		this.b = val;
	}

	public void systemWrite(Boolean val) {
		this.b = val;
	}

	public boolean isEmpty() {
		return false;
	}
}
