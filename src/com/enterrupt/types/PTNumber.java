package com.enterrupt.types;

import java.util.EnumSet;

public class PTNumber extends PTPrimitiveType<Double> {

	private static Type staticTypeFlag = Type.NUMBER;
	private Double d;

	protected PTNumber() {
		super(staticTypeFlag);
	}

	public Double read() {
		return this.d;
	}

	public void write(int newValue) {
        this.checkIsWriteable();
		this.d = new Double(newValue);
	}

    public void write(Double newValue) {
        this.checkIsWriteable();
        this.d = newValue;
    }

    public void systemWrite(Double newValue) {
        this.checkIsSystemWriteable();
        this.d = newValue;
    }

	public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof PTNumber))
            return false;

        PTNumber other = (PTNumber)obj;
        if(this.read().equals(other.read())) {
            return true;
        }
        return false;
    }

	public boolean typeCheck(PTType a) {
		return (a instanceof PTNumber &&
			this.getType() == a.getType());
	}

    public static PTNumber getSentinel() {

        // If the sentinel has already been cached, return it immediately.
        String cacheKey = getCacheKey();
        if(PTType.isSentinelCached(cacheKey)) {
            return (PTNumber)PTType.getCachedSentinel(cacheKey);
        }

        // Otherwise, create a new sentinel type and cache it before returning it.
        PTNumber sentinelObj = new PTNumber();
        PTType.cacheSentinel(sentinelObj, cacheKey);
        return sentinelObj;
    }

    public PTPrimitiveType alloc() {
        PTNumber newObj = new PTNumber();
        PTType.clone(this, newObj);
        return newObj;
    }

    private static String getCacheKey() {
        StringBuilder b = new StringBuilder(staticTypeFlag.name());
        return b.toString();
    }

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(super.toString());
		b.append(",d=").append(this.d);
		return b.toString();
	}
}
