package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;

public class PTRecord implements PTDataType {

	private Map<String, PTField> fields;

	public PTRecord(Record recDefn) {
		fields = new HashMap<String, PTField>();

		for(Map.Entry<String, RecordField> cursor : recDefn.fieldTable.entrySet()) {
			this.fields.put(cursor.getKey(), null);
		}
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
