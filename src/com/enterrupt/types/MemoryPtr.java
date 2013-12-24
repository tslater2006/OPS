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

	public abstract boolean isEmpty();
	public abstract String toString();

	public static void copy(MemoryPtr src, MemoryPtr dest) {

		if(src instanceof StringPtr && dest instanceof StringPtr) {
			((StringPtr) dest).write(((StringPtr) src).read());
		} else {
			throw new EntDataTypeException("Pointer types do not agree; unable to copy from source to destination.");
		}
	}

	public static boolean isEqual(MemoryPtr p1, MemoryPtr p2) {

		/**
		 * TODO: In the future, increase performance by checking if p1 and p2
		 * point to the same (Java) object; if that's the case, return true immediately.
		 */
		if(p1 instanceof StringPtr && p2 instanceof StringPtr) {
			return ((StringPtr) p1).read().equals(((StringPtr) p2).read());
		} else {
			throw new EntDataTypeException("Unable to determine if pointer values are equal; types do not agree.");
		}
	}
}
