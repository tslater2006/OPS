package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;

public class PTRowset extends PTObjectType {

	private static Type staticTypeFlag = Type.ROWSET;

	protected PTRowset() {
		super(staticTypeFlag);
	}

	public PTType dot(String s) {
		throw new EntDataTypeException("Need to implement dot() for PTRowset.");
	}

    public PTPrimitiveType castTo(PTPrimitiveType t) {
        throw new EntDataTypeException("castTo() has not been implemented.");
    }

	public boolean typeCheck(PTType a) {
		return (a instanceof PTRowset &&
			this.getType() == a.getType());
	}

    public static PTRowset getSentinel() {

        // If the sentinel has already been cached, return it immediately.
        String cacheKey = getCacheKey();
        if(PTType.isSentinelCached(cacheKey)) {
            return (PTRowset)PTType.getCachedSentinel(cacheKey);
        }

        // Otherwise, create a new sentinel type and cache it before returning it.
        PTRowset sentinelObj = new PTRowset();
        PTType.cacheSentinel(sentinelObj, cacheKey);
        return sentinelObj;
    }

    public PTRowset alloc() {
        PTRowset newObj = new PTRowset();
        PTType.clone(this, newObj);
        return newObj;
    }

    private static String getCacheKey() {
        StringBuilder b = new StringBuilder(staticTypeFlag.name());
        return b.toString();
    }

	@Override
	public String toString() {
		return super.toString();
	}
}
