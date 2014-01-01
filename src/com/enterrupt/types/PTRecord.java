package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;

public class PTRecord extends PTObjectType {

	private static Type staticTypeFlag = Type.RECORD;
	public Record recDefn;
	private Map<String, PTField> fields;

	protected PTRecord() {
		super(staticTypeFlag);
	}

	protected PTRecord(Record r) {
		super(staticTypeFlag);

		this.recDefn = r;
		this.fields = new HashMap<String, PTField>();
		for(Map.Entry<String, RecordField> cursor : r.fieldTable.entrySet()) {
			this.fields.put(cursor.getKey(),
				PTField.getSentinel().alloc(cursor.getValue()));
		}
	}

	public PTType dot(String s) {
		if(this.fields.containsKey(s)) {
			return this.fields.get(s);
		}
		throw new EntDataTypeException("No data member exists on PTRecord "
			+ "with s=" + s);
	}

    public PTPrimitiveType castTo(PTPrimitiveType t) {
        throw new EntDataTypeException("castTo() has not been implemented.");
    }

	public boolean typeCheck(PTType a) {
		return (a instanceof PTRecord &&
			this.getType() == a.getType());
	}

    public static PTRecord getSentinel() {

        // If the sentinel has already been cached, return it immediately.
        String cacheKey = getCacheKey();
        if(PTType.isSentinelCached(cacheKey)) {
            return (PTRecord)PTType.getCachedSentinel(cacheKey);
        }

        // Otherwise, create a new sentinel type and cache it before returning it.
        PTRecord sentinelObj = new PTRecord();
        PTType.cacheSentinel(sentinelObj, cacheKey);
        return sentinelObj;
    }

    /**
     * Allocated records must have an associated record defn in order
     * to determine the type of the value enclosed within them. However, this
     * defn is not part of the type itself; a Record variable can be assigned
     * any Record object, regardless of its underlying record defn.
     */
    public PTRecord alloc(Record recDefn) {
        PTRecord newObj = new PTRecord(recDefn);
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
