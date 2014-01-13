package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;
import com.enterrupt.runtime.*;

public class PTField extends PTObjectType {

	private static Type staticTypeFlag = Type.FIELD;
	private PTPrimitiveType value;
	public RecordField recFieldDefn;

	protected PTField() {
		super(staticTypeFlag);
	}

	protected PTField(RecordField rfd) {
		super(staticTypeFlag);
		this.recFieldDefn = rfd;

		switch(rfd.typeFlag) {
			case 0:
				this.value = (PTPrimitiveType)PTString.getSentinel().alloc();
				break;
			case 2:
				this.value = (PTPrimitiveType)PTNumber.getSentinel().alloc();
				break;
			case 4:
				this.value = (PTPrimitiveType)PTDate.getSentinel().alloc();
				break;
			default:
				throw new EntVMachRuntimeException("Unable to determine appropriate " +
					"PTPrimitiveType for PTField given record field type flag: "
					+ rfd.typeFlag);
		}
	}

	public void setDefault() {
		value.setDefault();
	}

	public PTPrimitiveType getValue() {
		return this.value;
	}

    public PTType dotProperty(String s) {
		if(s.equals("Value")) {
			return this.value;
		}
		return null;
    }

    public Callable dotMethod(String s) {
		return null;
    }

    /**
     * Calls to make a field read-only should make the
	 * field's value read-only as well.
     */
    @Override
    public PTType setReadOnly() {
        super.setReadOnly();
		if(this.value != null) {
			this.value.setReadOnly();
		}
        return this;
    }

    public PTPrimitiveType castTo(PTPrimitiveType t) {
		if(value.typeCheck(t)) {
			return value;
		}
        throw new EntDataTypeException("Unable to cast PTField (" + this + ") to the " +
			"type requested: " + t);
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
		b.append(":").append(recFieldDefn.FIELDNAME);
		b.append(",value=").append(value.toString());
		return b.toString();
	}
}
