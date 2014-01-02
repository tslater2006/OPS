package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;
import com.enterrupt.runtime.*;

public class PTRecord extends PTObjectType {

	private static Type staticTypeFlag = Type.RECORD;
	public Record recDefn;
	public Map<String, PTField> fields;

	protected PTRecord() {
		super(staticTypeFlag);
	}

	protected PTRecord(Record r) {
		super(staticTypeFlag);

		this.recDefn = r;
		// this map is linked in order to preserve the order in which fields are added.
		this.fields = new LinkedHashMap<String, PTField>();
		for(Map.Entry<String, RecordField> cursor : r.fieldTable.entrySet()) {
			this.fields.put(cursor.getKey(),
				PTField.getSentinel().alloc(cursor.getValue()));
		}
	}

    public PTType dotProperty(String s) {
		return this.fields.get(s);
    }

    public Callable dotMethod(String s) {
		return null;
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
