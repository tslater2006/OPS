package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;
import com.enterrupt.runtime.*;

public class PTRecord extends PTObjectType {

	public Record recDefn;
	private Map<String, PTField> fields;

	protected PTRecord(Record r) {
		super(Type.RECORD);
		this.recDefn = r;
		for(Map.Entry<String, RecordField> cursor : r.fieldTable.entrySet()) {
			this.fields.put(cursor.getKey(),
				PTType.getSentinel(Type.RECORD).alloc(cursor.getValue()));
		}
	}

	public PTType dot(String s) {
		throw new EntVMachRuntimeException("Need to implement dot() for PTRecord.");
	}
}
