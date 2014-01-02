package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;
import com.enterrupt.runtime.*;
import java.lang.reflect.*;
import org.apache.logging.log4j.*;

public class PTRowset extends PTObjectType {

	private static Type staticTypeFlag = Type.ROWSET;
	public Record recDefn;
	private List<PTRecord> rows;
	private static Map<String, Method> ptMethodTable;

	private static Logger log = LogManager.getLogger(PTRowset.class.getName());

	static {
		Method[] methods = PTRowset.class.getMethods();
    	ptMethodTable = new HashMap<String, Method>();
		for(Method m : methods) {
			if(m.getName().indexOf("PT_") == 0) {
				ptMethodTable.put(m.getName().substring(3), m);
			}
		}
	}

	protected PTRowset() {
		super(staticTypeFlag);
	}

	protected PTRowset(Record r) {
		super(staticTypeFlag);

		this.recDefn = r;
		this.rows = new ArrayList<PTRecord>();

		// One row is always present in the rowset, even when flushed.
		this.rows.add(PTRecord.getSentinel().alloc(this.recDefn));
	}

    public PTType dotProperty(String s) {
		return null;
    }

    public Callable dotMethod(String s) {
		if(ptMethodTable.containsKey(s)) {
			return new Callable(ptMethodTable.get(s), this);
		}
		return null;
    }

	public void PT_Flush() {
        List<PTType> args = Environment.getArgsFromCallStack();
        if(args.size() != 0) {
            throw new EntVMachRuntimeException("Expected zero arguments.");
        }

		// One row is always present in the rowset, even when flushed.
		this.rows.clear();
		this.rows.add(PTRecord.getSentinel().alloc(this.recDefn));
	}

	public void PT_Fill() {
        List<PTType> args = Environment.getArgsFromCallStack();
        if(args.size() < 1) {
            throw new EntVMachRuntimeException("Expected at least one string arg.");
        }

		throw new EntVMachRuntimeException("Implement Fill.");
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

    /**
     * Allocated rowsets must have an associated record defn in order
     * to determine the type of the value enclosed within them. However, this
     * defn is not part of the type itself; a Rowset variable can be assigned
     * any Rowset object, regardless of its underlying record defn.
     */
    public PTRowset alloc(Record recDefn) {
        PTRowset newObj = new PTRowset(recDefn);
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
