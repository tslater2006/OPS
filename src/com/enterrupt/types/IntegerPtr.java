package com.enterrupt.types;

import java.util.EnumSet;

public class IntegerPtr extends MemoryPtr<Integer> {

	private Integer i;

	public IntegerPtr() {
		super(MFlag.INTEGER);
		this.i = 0;
	}

	public IntegerPtr(MFlag f) {
		super(EnumSet.of(MFlag.INTEGER, f));
		this.i = 0;
	}

	public IntegerPtr(Integer initial) {
		super(MFlag.INTEGER);
		this.i = initial;
	}

	public IntegerPtr(Integer initial, MFlag f) {
		super(EnumSet.of(MFlag.INTEGER, f));
		this.i = initial;
	}

	public Integer read() {
		return this.i;
	}

	public void write(Integer val) {
		if(this.flags.contains(MFlag.READ_ONLY)) {
			System.out.println("[ERROR] Attempted to write to read-only location.");
			System.exit(1);
		}
		this.i = val;
	}

	public void systemWrite(Integer val) {
		this.i = val;
	}

	public boolean isEmpty() {
		System.out.println("[ERROR] Have not implemented isEmpty for IntegerPtr yet.");
		System.exit(1);
		return false;
	}
}
