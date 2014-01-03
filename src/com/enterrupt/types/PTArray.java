package com.enterrupt.types;

import com.enterrupt.runtime.*;
import java.util.*;
import org.apache.logging.log4j.*;
import java.lang.reflect.*;

public class PTArray extends PTObjectType {

	private static Logger log = LogManager.getLogger(PTArray.class.getName());
	private static Type staticTypeFlag = Type.ARRAY;
	public int dimensions;
	public PTType baseType;
	public List<PTType> values;
	private static Map<String, Method> ptMethodTable;

	static {
		// cache pointers to PeopleTools Array methods.
		Method[] methods = PTArray.class.getMethods();
		ptMethodTable = new HashMap<String, Method>();
		for(Method m : methods) {
			if(m.getName().indexOf("PT_") == 0) {
				ptMethodTable.put(m.getName().substring(3), m);
			}
		}
	}

	protected PTArray(int d, PTType b) {
		super(staticTypeFlag);

		if(d == 1 && b instanceof PTArray) {
			throw new EntVMachRuntimeException("Single dimension arrays cannot " +
				"have a base type of PTArray.");
		}

		this.dimensions = d;
		this.baseType = b;
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

    public PTPrimitiveType castTo(PTPrimitiveType t) {
        throw new EntDataTypeException("castTo() has not been implemented.");
    }

	public boolean typeCheck(PTType a) {
		return (a instanceof PTArray  &&
			this.getType() == a.getType() &&
			this.dimensions == ((PTArray)a).dimensions &&
			this.baseType.typeCheck(((PTArray)a).baseType));
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
		newObj.initValues();
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
		b.append(",values=").append(this.values);
		return b.toString();
	}

	protected void initValues() {
		if(this.isSentinel()) {
			throw new EntVMachRuntimeException("Attempted to initialize the values " +
				"array for a sentinel PTArray object.");
		}
		this.values = new ArrayList<PTType>();
	}

	public void PT_Push() {
        List<PTType> args = Environment.getArgsFromCallStack();
        if(args.size() != 1) {
            throw new EntVMachRuntimeException("Expected one argument.");
        }

		throw new EntVMachRuntimeException("Push() on PTArray has not been implemented.");
	}
}

