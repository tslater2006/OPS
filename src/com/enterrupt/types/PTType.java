package com.enterrupt.types;

import java.util.*;
import com.enterrupt.runtime.*;
import com.enterrupt.pt.*;

public class PTType {

	private Type type;
	private EnumSet<TFlag> flags;
	private boolean isSentinel;

	// For arrays (i.e., "array of array of string")
	private int arrayDimensions;
	private Type baseType;

	private static Map<String, PTType> sentinelCache;
	static {
		sentinelCache = new HashMap<String, PTType>();
	}

	protected PTType(Type t) {
		this.type = t;
		this.flags = EnumSet.noneOf(TFlag.class);
	}

	public static PTType getSentinel(Type t) {

		// If the type has already been cached, return it immediately.
		String cacheKey = generateCacheKey(t);
		if(sentinelCache.containsKey(cacheKey)) {
			return sentinelCache.get(cacheKey);
		}

		// OTherwise, create a new sentinel type and cache it before returning it.
		PTType sentinelObj = enumToType(t);
		sentinelObj.setSentinel();
		sentinelCache.put(cacheKey, sentinelObj);
		return sentinelObj;
	}

	public static PTType getSentinel(Type t, int dim, Type baseType) {
		throw new EntVMachRuntimeException("Implement getSentinel for array types.");
	}

	public PTType alloc() {
		PTType newObj = enumToType(this.type);
		clone(this, newObj);
		return newObj;
	}

	public PTRecord alloc(Record recDefn) {
		PTRecord recObj = new PTRecord(recDefn);
		clone(this, recObj);
		return recObj;
	}

	public PTField alloc(RecordField recFieldDefn) {
		PTField fldObj = new PTField(recFieldDefn);
		clone(this, fldObj);
		return fldObj;
	}

	private void clone(PTType src, PTType dest) {
		if(src.type != dest.type) {
			throw new EntVMachRuntimeException("Attempted to clone PTType objects " +
				"with different types (" + src.type + " to " + dest.type + ")");
		}
		dest.setType(src.getType());
		dest.setFlags(src.getFlags());
	}

	private static PTType enumToType(Type t) {
		switch(t) {
			case STRING:
				return new PTString();
			case BOOLEAN:
				return new PTBoolean();
			case DEFN_LITERAL:
				return new PTDefnLiteral();
			default:
				throw new EntVMachRuntimeException("Unable to match type:" +
					t + " to the appropriate PTType subclass.");
		}
	}

	private static String generateCacheKey(Type t) {
		/**
		 * TODO: Arrays will need to include dimensions and base type.
		 */
		StringBuilder b = new StringBuilder();
		b.append("t:").append(t);
		return b.toString();
	}

	public PTType setReadOnly() {
		if(this.isSentinel()) {
			throw new EntVMachRuntimeException("Encountered illegal attempt to " +
				"make a sentinel PTType readonly.");
		}
		this.flags.add(TFlag.READONLY);
		return this;
	}

	protected boolean isSentinel() {
		return this.isSentinel;
	}

	private PTType setSentinel() {
		this.isSentinel = true;
		return this;
	}

	private Type getType() {
		return this.type;
	}

	private void setType(Type t) {
		if(this.isSentinel()) {
			throw new EntVMachRuntimeException("Encountered illegal attempt to " +
				"set the type of a sentinel PTType.");
		}
		this.type = t;
	}

	private EnumSet<TFlag> getFlags() {
		return this.flags;
	}

	private void setFlags(EnumSet<TFlag> fSet) {
		if(this.isSentinel()) {
			throw new EntVMachRuntimeException("Encountered illegal attempt to " +
				"set the flags of a sentinel PTType.");
		}
		this.flags = fSet.clone();
	}
}
