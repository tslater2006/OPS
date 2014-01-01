package com.enterrupt.types;

public class PTArray extends PTObjectType {

	private static Type staticTypeFlag = Type.ARRAY;
	public int dimensions;
	public PTType baseType;

	protected PTArray(int d, PTType b) {
		super(staticTypeFlag);
		this.dimensions = d;
		this.baseType = b;
	}

	public PTType dot(String s) {
		throw new EntDataTypeException("Need to support dot() on arrays.");
	}

	public void assgmtDelegate(PTPrimitiveType src) {
		throw new EntDataTypeException("assgmtDelegate for arrays " +
			"is not yet supported.");
	}

	public boolean typeCheck(PTType a) {
		return (a instanceof PTArray  &&
			this.getType() == a.getType() &&
			this.dimensions == ((PTArray)a).dimensions &&
			this.baseType == ((PTArray)a).baseType);
	}

    public static PTArray getSentinel(int arrDimensions, PTType baseType) {

        // If the sentinel has already been cached, return it immediately.
        String cacheKey = getCacheKey(arrDimensions, baseType);
        if(PTType.isSentinelCached(cacheKey)) {
            return (PTArray)PTType.getCachedSentinel(cacheKey);
        }

        // Otherwise, create a new sentinel type and cache it before returning it.
        PTArray sentinelObj = new PTArray(arrDimensions, baseType);
        PTType.cacheSentinel(sentinelObj, cacheKey);
        return sentinelObj;
    }

    public PTArray alloc() {
        PTArray newObj = new PTArray(this.dimensions, this.baseType);
        PTType.clone(this, newObj);
        return newObj;
    }

    private static String getCacheKey(int arrDimensions, PTType baseType) {
        StringBuilder b = new StringBuilder(staticTypeFlag.name());
        b.append(":d=").append(arrDimensions);
		b.append(",baseType=").append(baseType);
        return b.toString();
    }

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(super.toString());
		b.append(",dim=").append(this.dimensions);
		b.append(",baseType=").append(this.baseType.toString());
		return b.toString();
	}
}

