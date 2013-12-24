package com.enterrupt.types;

public abstract class PrimitivePtr<T> extends MemoryPtr {

	public PrimitivePtr() {
		super();
	}

	public PrimitivePtr(MFlag f) {
		super(f);
	}

    public abstract T read();
    public abstract void write(T val);
    public abstract void systemWrite(T val);
    public abstract boolean isEmpty();
    public abstract String toString();
}
