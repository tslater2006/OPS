package com.enterrupt.types;

import java.util.EnumSet;
import com.enterrupt.runtime.*;

public class PTBoolean extends PTPrimitiveType<Boolean> {

	private static Type staticTypeFlag = Type.BOOLEAN;
	private Boolean b;

	protected PTBoolean() {
		super(staticTypeFlag);
	}

	public Boolean read() {
		return this.b;
	}

    public void write(Boolean newValue) {
        this.checkIsWriteable();
        this.b = newValue;
    }

    public void systemWrite(Boolean newValue) {
        this.checkIsSystemWriteable();
        this.b = newValue;
    }

    public PTBoolean isEqual(PTPrimitiveType op) {
		if(!(op instanceof PTBoolean)) {
			throw new EntDataTypeException("Expected op to be PTBoolean.");
		}
		if(this.b.equals(((PTBoolean)op).read())) {
			return Environment.TRUE;
		}
		return Environment.FALSE;
	}

    public PTBoolean isGreaterThan(PTPrimitiveType op) {
		throw new EntDataTypeException("isGreaterThan is not supported for " +
			"booleans.");
	}

    public PTBoolean isLessThan(PTPrimitiveType op) {
		throw new EntDataTypeException("isLessThan is not supported for " +
			"booleans.");
	}

    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof PTBoolean))
            return false;

        PTBoolean other = (PTBoolean)obj;
		if(this.read().equals(other.read())) {
            return true;
        }
        return false;
    }

	public boolean typeCheck(PTType a) {
		return (a instanceof PTBoolean &&
			this.getType() == a.getType());
	}

    public static PTBoolean getSentinel() {

        // If the sentinel has already been cached, return it immediately.
        String cacheKey = getCacheKey();
        if(PTType.isSentinelCached(cacheKey)) {
            return (PTBoolean)PTType.getCachedSentinel(cacheKey);
        }

        // Otherwise, create a new sentinel type and cache it before returning it.
        PTBoolean sentinelObj = new PTBoolean();
        PTType.cacheSentinel(sentinelObj, cacheKey);
        return sentinelObj;
    }

    public PTPrimitiveType alloc() {
        PTBoolean newObj = new PTBoolean();
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
		b.append(",b=").append(this.b);
		return b.toString();
	}
}

