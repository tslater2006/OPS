package com.enterrupt.types;

import java.util.EnumSet;
import com.enterrupt.runtime.*;

public class PTString extends PTPrimitiveType<String> {

	private static Type staticTypeFlag = Type.STRING;
	private String s;

	protected PTString() {
		super(staticTypeFlag);
	}

	public String read() {
		return this.s;
	}

	public void write(String newValue) {
		this.checkIsWriteable();
		this.s = newValue;
	}

	public void systemWrite(String newValue) {
		this.checkIsSystemWriteable();
		this.s = newValue;
	}

	public void setDefault() {
		this.s = " ";
	}

	public PTPrimitiveType add(PTPrimitiveType op) {
		throw new EntVMachRuntimeException("add() not supported.");
	}

    public PTBoolean isEqual(PTPrimitiveType op) {
		if(!(op instanceof PTString)) {
			throw new EntDataTypeException("Expected op to be PTString.");
		}
		if(this.s.equals(((PTString)op).read())) {
			return Environment.TRUE;
		}
		return Environment.FALSE;
    }

    public PTBoolean isGreaterThan(PTPrimitiveType op) {
        if(!(op instanceof PTString)) {
            throw new EntDataTypeException("Expected op to be PTString.");
        }
        if(this.s.compareTo(((PTString)op).read()) > 0) {
            return Environment.TRUE;
        }
        return Environment.FALSE;
    }

    public PTBoolean isLessThan(PTPrimitiveType op) {
        if(!(op instanceof PTString)) {
            throw new EntDataTypeException("Expected op to be PTString.");
        }
        if(this.s.compareTo(((PTString)op).read()) < 0) {
            return Environment.TRUE;
        }
        return Environment.FALSE;
	}

	public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof PTString))
            return false;

        PTString other = (PTString)obj;
        if(this.read().equals(other.read())) {
            return true;
        }
        return false;
    }

	public boolean typeCheck(PTType a) {
		return (a instanceof PTString &&
			this.getType() == a.getType());
	}

    public static PTString getSentinel() {

        // If the sentinel has already been cached, return it immediately.
        String cacheKey = getCacheKey();
        if(PTType.isSentinelCached(cacheKey)) {
            return (PTString)PTType.getCachedSentinel(cacheKey);
        }

        // Otherwise, create a new sentinel type and cache it before returning it.
        PTString sentinelObj = new PTString();
        PTType.cacheSentinel(sentinelObj, cacheKey);
        return sentinelObj;
    }

    public PTPrimitiveType alloc() {
        PTString newObj = new PTString();
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
		b.append(",s=").append(s);
		return b.toString();
	}
}
