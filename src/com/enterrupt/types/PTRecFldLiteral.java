package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;
import com.enterrupt.runtime.*;

public class PTRecFldLiteral extends PTObjectType {

	private static Type staticTypeFlag = Type.REC_FLD_LITERAL;
	public String RECNAME;
	public String FIELDNAME;
	private Record recDefn;

	protected PTRecFldLiteral() {
		super(staticTypeFlag);
	}

	protected PTRecFldLiteral(Record r) {
		super(staticTypeFlag);
		this.RECNAME = r.RECNAME;
		this.recDefn = r;
	}

	/**
	 * Dot accesses on record field literals must
	 * always return this object, with the field
	 * value set to the GENERIC_ID following the dot operator.
	 */
    public PTType dotProperty(String s) {
		List<RecordField> rfList = this.recDefn.getExpandedFieldList();
		for(RecordField rf : rfList) {
			if(rf.FIELDNAME.equals(s)) {
				this.FIELDNAME = s;
				return this;
			}
		}

		throw new EntVMachRuntimeException("Unable to resolve s=" +
			s + " to a field on the PTRecFldLiteral for record " +
			this.RECNAME);
    }

    public Callable dotMethod(String s) {
		return null;
    }

    public PTPrimitiveType castTo(PTPrimitiveType t) {
        throw new EntDataTypeException("castTo() has not been implemented.");
    }

	public boolean typeCheck(PTType a) {
		return (a instanceof PTRecFldLiteral &&
			this.getType() == a.getType());
	}

    public static PTRecFldLiteral getSentinel() {

        // If the sentinel has already been cached, return it immediately.
        String cacheKey = getCacheKey();
        if(PTType.isSentinelCached(cacheKey)) {
            return (PTRecFldLiteral)PTType.getCachedSentinel(cacheKey);
        }

        // Otherwise, create a new sentinel type and cache it before returning it.
        PTRecFldLiteral sentinelObj = new PTRecFldLiteral();
        PTType.cacheSentinel(sentinelObj, cacheKey);
        return sentinelObj;
    }

    public PTRecFldLiteral alloc(Record recDefn) {
        PTRecFldLiteral newObj = new PTRecFldLiteral(recDefn);
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
        b.append(",RECNAME=").append(this.RECNAME);
		b.append(",FIELDNAME=").append(this.FIELDNAME);
        return b.toString();
    }
}
