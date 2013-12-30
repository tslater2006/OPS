package com.enterrupt.types;

import java.util.*;
import com.enterrupt.pt.*;
import com.enterrupt.pt.peoplecode.*;

public class PTType {

	private Type type;
	private EnumSet<TFlag> flags;
	private boolean isSentinel;

	// For arrays (i.e., "array of array of string")
	private int arrayDimensions;
	private Type baseType;

	private static Map<String, PTType> stdSentinelCache;
	private static Map<String, PTAppClassObj> appClassObjSentinelCache;
	private static Map<String, PTArray> arraySentinelCache;
	static {
		stdSentinelCache = new HashMap<String, PTType>();
		appClassObjSentinelCache = new HashMap<String, PTAppClassObj>();
		arraySentinelCache = new HashMap<String, PTArray>();
	}

	protected PTType(Type t) {
		this.type = t;
		this.flags = EnumSet.noneOf(TFlag.class);
	}

	public static PTType getSentinel(Type t) {

		// If the type has already been cached, return it immediately.
		if(stdSentinelCache.containsKey(t.toString())) {
			return stdSentinelCache.get(t.toString());
		}

		// OTherwise, create a new sentinel type and cache it before returning it.
		PTType sentinelObj = enumToType(t);
		sentinelObj.setSentinel();
		stdSentinelCache.put(t.toString(), sentinelObj);
		return sentinelObj;
	}

	public static PTAppClassObj getSentinel(AppClassPeopleCodeProg prog) {

		// If the type has already been cached, return it immediately.
		if(appClassObjSentinelCache.containsKey(prog.getDescriptor())) {
			return appClassObjSentinelCache.get(prog.getDescriptor());
		}

		// OTherwise, create a new sentinel type and cache it before returning it.
		PTAppClassObj sentinelObj = new PTAppClassObj(prog);
		sentinelObj.setSentinel();
		appClassObjSentinelCache.put(prog.getDescriptor(), sentinelObj);
		return sentinelObj;
	}

	public static PTArray getSentinel(int arrDimensions, PTType baseType) {

		String cacheKey = "d=" + arrDimensions + ",baseType=" + baseType.getType();

		// If the type has already been cached, return it immediately.
		if(arraySentinelCache.containsKey(cacheKey)) {
			return arraySentinelCache.get(cacheKey);
		}

		// OTherwise, create a new sentinel type and cache it before returning it.
		PTArray sentinelObj = new PTArray(arrDimensions, baseType);
		sentinelObj.setSentinel();
		arraySentinelCache.put(cacheKey, sentinelObj);
		return sentinelObj;
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
			throw new EntDataTypeException("Attempted to clone PTType objects " +
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
			case RECORD:
				return new PTRecord();
			case FIELD:
				return new PTField();
			case DATE:
				return new PTDate();
			case INTEGER:
				return new PTInteger();
			case ROWSET:
				return new PTRowset();
			default:
				throw new EntDataTypeException("Unable to match type:" +
					t + " to the appropriate PTType subclass.");
		}
	}

	public PTType setReadOnly() {
		if(this.isSentinel()) {
			throw new EntDataTypeException("Encountered illegal attempt to " +
				"make a sentinel PTType readonly.");
		}
		this.flags.add(TFlag.READONLY);
		return this;
	}

	protected boolean isSentinel() {
		return this.isSentinel;
	}

	protected PTType setSentinel() {
		this.isSentinel = true;
		return this;
	}

	private Type getType() {
		return this.type;
	}

	private void setType(Type t) {
		if(this.isSentinel()) {
			throw new EntDataTypeException("Encountered illegal attempt to " +
				"set the type of a sentinel PTType.");
		}
		this.type = t;
	}

	protected EnumSet<TFlag> getFlags() {
		return this.flags;
	}

	private void setFlags(EnumSet<TFlag> fSet) {
		if(this.isSentinel()) {
			throw new EntDataTypeException("Encountered illegal attempt to " +
				"set the flags of a sentinel PTType.");
		}
		this.flags = fSet.clone();
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		if(this.isSentinel) { b.append("SENTINEL:"); }
		b.append(this.type).append(this.flags);
		return b.toString();
	}
}
