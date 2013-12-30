package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;

public class PTRecord extends PTObjectType {

	public Record recDefn;
	private Map<String, PTField> fields;

	protected PTRecord() {
		super(Type.RECORD);
	}

	protected PTRecord(Record r) {
		super(Type.RECORD);

		this.recDefn = r;
		this.fields = new HashMap<String, PTField>();
		for(Map.Entry<String, RecordField> cursor : r.fieldTable.entrySet()) {
			this.fields.put(cursor.getKey(),
				PTType.getSentinel(Type.FIELD).alloc(cursor.getValue()));
		}
	}

	public PTType dot(String s) {
		throw new EntDataTypeException("Need to implement dot() for PTRecord.");
	}
}
