package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;

public class RecordPtr extends MemoryPtr {

	private Map<String, FieldPtr> fieldPtrs;

	public RecordPtr(Record recDefn) {
		super();
		fieldPtrs = new HashMap<String, FieldPtr>();

		for(Map.Entry<String, RecordField> cursor : recDefn.fieldTable.entrySet()) {
			this.fieldPtrs.put(cursor.getKey(), null);
		}
	}

	public boolean isEmpty() {
		return false;
	}

	public String toString() {
		return fieldPtrs.toString();
	}
}
