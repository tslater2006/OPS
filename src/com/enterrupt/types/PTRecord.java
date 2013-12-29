package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;
import com.enterrupt.memory.*;

public abstract class PTRecord implements PTDataType {

	public Record recDefn;
	protected Map<String, Pointer> fields;

	public PTRecord(Record r) {
		this.recDefn = r;
		this.fields = new HashMap<String, Pointer>();
	}

	public Pointer access(String s) {
		Pointer ptr = this.fields.get(s);
		if(ptr == null) {
			throw new EntDataTypeException("Call to access(s) on PTRecord with " +
				" s=" + s + " did not match any fields.");
		}
		return ptr;
	}

	public abstract boolean equals(Object obj);
	public abstract String toString();
}
