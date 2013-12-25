package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;

public class PTRecord implements PTDataType {

	public Record recDefn;
	private Map<String, MemPointer> fields;

	public PTRecord(Record r) {
		this.recDefn = r;
		this.fields = new HashMap<String, MemPointer>();

		for(Map.Entry<String, RecordField> cursor : r.fieldTable.entrySet()) {
			this.fields.put(cursor.getKey(), new MemPointer(new PTField()));
		}
	}

	public MemPointer access(String s) {
		MemPointer ptr = this.fields.get(s);
		if(ptr == null) {
			throw new EntDataTypeException("Call to access(s) on PTRecord with " +
				" s=" + s + " did not match any fields.");
		}
		return ptr;
	}

	public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof PTRecord))
            return false;

        PTRecord other = (PTRecord)obj;
		throw new EntDataTypeException("equals() for PTRecord not yet implemented.");
    }

	public String toString() {
		return fields.toString();
	}
}
