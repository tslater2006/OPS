package com.enterrupt.types;

import java.util.*;
import com.enterrupt.pt.*;
import com.enterrupt.pt.peoplecode.*;

public abstract class PTType {

	private Type type;
	private EnumSet<TFlag> flags;
	private boolean sentinelFlag;

	private static Map<String, PTType> sentinelCache;
	static {
		sentinelCache = new HashMap<String, PTType>();
	}

	public abstract PTType dot(String s);
	public abstract boolean typeCheck(PTType a);

	protected PTType(Type t) {
		this.type = t;
		this.flags = EnumSet.noneOf(TFlag.class);
	}

	protected static boolean isSentinelCached(String key) {
		return sentinelCache.containsKey(key);
	}

	protected static PTType getCachedSentinel(String key) {
		return sentinelCache.get(key);
	}

	protected static void cacheSentinel(PTType sentinelObj, String cacheKey) {
		sentinelObj.setSentinel();
		sentinelCache.put(cacheKey, sentinelObj);
	}

	protected static void clone(PTType src, PTType dest) {
		if(src.type != dest.type) {
			throw new EntDataTypeException("Attempted to clone PTType objects " +
				"with different type enum flags (" + src.type + " to " + dest.type + ")");
		}
		dest.setType(src.getType());
		dest.setFlags(src.getFlags());

		if(src instanceof PTArray) {
			((PTArray)dest).dimensions = ((PTArray)src).dimensions;
			((PTArray)dest).baseType = ((PTArray)src).baseType;
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
		return this.sentinelFlag;
	}

	private PTType setSentinel() {
		this.sentinelFlag = true;
		return this;
	}

	protected Type getType() {
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
		if(this.isSentinel()) { b.append("SENTINEL:"); }
		b.append(this.type).append(this.flags);
		return b.toString();
	}
}
