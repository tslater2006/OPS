package com.enterrupt.types;

import java.util.EnumSet;

public abstract class MemoryPtr<T> {

	public EnumSet<MFlag> flags;

    public MemoryPtr() {
        this.flags = EnumSet.noneOf(MFlag.class);
    }

    public MemoryPtr(MFlag flag) {
        this.flags = EnumSet.of(flag);
    }

    public MemoryPtr(EnumSet<MFlag> flagSet) {
        this.flags = EnumSet.copyOf(flagSet);
    }

	public abstract T read();
	public abstract void write(T val);
	public abstract void systemWrite(T val);
	public abstract boolean isEmpty();

	public static void copy(MemoryPtr src, MemoryPtr dest) {

		if(src.flags.contains(MFlag.STRING) && dest.flags.contains(MFlag.STRING)) {
			((StringPtr) dest).write(((StringPtr) src).read());
		} else {
			System.out.println("[ERROR] Pointer types do not agree; unable to copy from source to destination.");
			System.exit(1);
		}
	}
}
