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
		if(this.fields.containsKey(s)) {
			return this.fields.get(s);
		}
		throw new EntDataTypeException("No data member exists on PTRecord "
			+ "with s=" + s);
	}

	public void assgmtDelegate(PTPrimitiveType src) {
		throw new EntDataTypeException("assgmtDelegate for record objects " +
			"is not yet supported.");
	}

	public boolean typeCheck(PTType a) {
		return (a instanceof PTRecord &&
			this.getType() == a.getType());
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
