package com.enterrupt.memory;

import java.util.EnumSet;
import com.enterrupt.types.*;

public abstract class Pointer {

	public EnumSet<MFlag> flags;
	public PTDataType target;

	public abstract void assign(PTDataType newTarget);
	public abstract String toString();

	public Pointer() {
		this.flags = EnumSet.noneOf(MFlag.class);
	}

	public Pointer(MFlag f) {
		this.flags = EnumSet.of(f);
	}

    public Pointer(EnumSet<MFlag> fSet) {
        this.flags = fSet;
    }

	public boolean isInitialized() {
		return target == null;
	}

	public PTDataType dereference() {
		return this.target;
	}
}
