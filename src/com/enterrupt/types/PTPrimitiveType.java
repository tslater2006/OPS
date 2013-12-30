package com.enterrupt.types;

public abstract class PTPrimitiveType<T> extends PTType {

	protected PTPrimitiveType(Type t) {
		super(t);
	}

	public abstract T read();
	public abstract void write(T newValue);
	public abstract void systemWrite(T newValue);

	public abstract boolean equals(Object obj);

	protected void checkIsWriteable() {
        if(this.isSentinel()) {
            throw new EntDataTypeException("Attempted illegal write to a " +
                "sentinel PTType object.");
        }
		if(this.getFlags().contains(TFlag.READONLY)) {
			throw new EntDataTypeException("Attempted illegal write to a " +
				"readonly PTType object.");
		}
	}

	protected void checkIsSystemWriteable() {
        if(this.isSentinel()) {
            throw new EntDataTypeException("Attempted illegal system write " +
                "to a sentinel PTType object.");
        }
	}

	/**
	 * TODO: Enable.
	 */
    //public abstract String toString();
}
