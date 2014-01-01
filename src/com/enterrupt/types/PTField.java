package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;

public class PTField extends PTObjectType {

	private static Type staticTypeFlag = Type.FIELD;
	private PTPrimitiveType value;

	protected PTField() {
		super(staticTypeFlag);
	}

	protected PTField(RecordField recFieldDefn) {
		super(staticTypeFlag);
		/**
		 * TODO: Determine type based on field metadata.
		 */
		this.value = (PTPrimitiveType)PTString.getSentinel().alloc();
	}

	public PTPrimitiveType getValue() {
		return this.value;
	}

	public PTType dot(String s) {
		throw new EntDataTypeException("Need to implement dot() for PTField.");
	}

	/**
	 * Primitive assignments to a Field should assign to
	 * the field's underlying value; i.e., SSR_STDNT_TERM0.EMPLID = %EmployeeId
	 */
	public void assgmtDelegate(PTPrimitiveType src) {
		this.value.copyValueFrom(src);
	}

	public boolean typeCheck(PTType a) {
		return (a instanceof PTField &&
			this.getType() == a.getType());
	}

    public static PTField getSentinel() {

        // If the sentinel has already been cached, return it immediately.
        String cacheKey = getCacheKey();
        if(PTType.isSentinelCached(cacheKey)) {
            return (PTField)PTType.getCachedSentinel(cacheKey);
        }

        // Otherwise, create a new sentinel type and cache it before returning it.
        PTField sentinelObj = new PTField();
        PTType.cacheSentinel(sentinelObj, cacheKey);
        return sentinelObj;
    }

	/**
	 * Allocated fields must have an associated record field defn in order
	 * to determine the type of the value enclosed within them. However, this
	 * defn is not part of the type itself; a Field variable can be assigned
	 * any Field object, regardless of its underlying record field defn.
	 */
    public PTField alloc(RecordField recFieldDefn) {
        PTField newObj = new PTField(recFieldDefn);
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
		b.append(",value=").append(value.toString());
		return b.toString();
	}
}
