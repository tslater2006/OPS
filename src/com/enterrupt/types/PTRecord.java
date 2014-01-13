package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;
import java.lang.reflect.*;
import com.enterrupt.runtime.*;

public class PTRecord extends PTObjectType {

	private static Type staticTypeFlag = Type.RECORD;
	public Record recDefn;
	public Map<String, PTField> fields;
	public Map<Integer, PTField> fieldIdxTable;
	private static Map<String, Method> ptMethodTable;

    static {
        // cache pointers to PeopleTools Record methods.
        Method[] methods = PTRecord.class.getMethods();
        ptMethodTable = new HashMap<String, Method>();
        for(Method m : methods) {
            if(m.getName().indexOf("PT_") == 0) {
                ptMethodTable.put(m.getName().substring(3), m);
            }
        }
    }


	protected PTRecord() {
		super(staticTypeFlag);
	}

	protected PTRecord(Record r) {
		super(staticTypeFlag);

		this.recDefn = r;
		// this map is linked in order to preserve the order in which fields are added.
		this.fields = new LinkedHashMap<String, PTField>();
		this.fieldIdxTable = new LinkedHashMap<Integer, PTField>();
		int i = 1;
		for(Map.Entry<String, RecordField> cursor : r.fieldTable.entrySet()) {
		 	PTField newFld = PTField.getSentinel().alloc(cursor.getValue());
			this.fields.put(cursor.getKey(), newFld);
			this.fieldIdxTable.put(i++, newFld);
		}
	}

    public PTType dotProperty(String s) {
		return this.fields.get(s);
    }

    public Callable dotMethod(String s) {
       if(ptMethodTable.containsKey(s)) {
            return new Callable(ptMethodTable.get(s), this);
        }
        return null;
    }

	public PTField getField(String fldName) {
		if(!this.fields.containsKey(fldName)) {
			throw new EntVMachRuntimeException("Call to getField with fldname=" +
				fldName + " did not match any field on this record: " + this.toString());
		}
		return this.fields.get(fldName);
	}

	public void setDefault() {
		for(Map.Entry<String, PTField> cursor : this.fields.entrySet()) {
			cursor.getValue().setDefault();
		}
	}

	public void PT_SetDefault() {
        List<PTType> args = Environment.getArgsFromCallStack();
        if(args.size() != 0) {
            throw new EntVMachRuntimeException("Expected no args.");
        }

		this.setDefault();
	}

	public void PT_SelectByKeyEffDt() {
        List<PTType> args = Environment.getArgsFromCallStack();
	    if(args.size() != 1 || (!(args.get(0) instanceof PTDate))) {
            throw new EntVMachRuntimeException("Expected single date arg.");
        }

		throw new EntVMachRuntimeException("Must implement SelectByKeyEffDt.");
	}

	/**
	 * Calls to make a record read-only should make its
	 * fields read-only as well.
	 */
	@Override
    public PTType setReadOnly() {
		super.setReadOnly();
		if(fields != null) {
			for(Map.Entry<String, PTField> cursor : fields.entrySet()) {
				cursor.getValue().setReadOnly();
			}
		}
        return this;
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
		StringBuilder b = new StringBuilder(super.toString());
		b.append(":").append(recDefn.RECNAME);
		b.append(",fields=").append(this.fields);
		return b.toString();
	}
}
