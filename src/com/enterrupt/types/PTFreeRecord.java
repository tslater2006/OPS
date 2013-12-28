package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;

public class PTFreeRecord extends PTRecord implements PTDataType {

	public PTFreeRecord(Record r) {
		super(r);

		for(Map.Entry<String, RecordField> cursor : r.fieldTable.entrySet()) {
			this.fields.put(cursor.getKey(), new StdPointer(
				new PTFreeField()));
		}
	}

	public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof PTFreeRecord))
            return false;

        PTFreeRecord other = (PTFreeRecord)obj;
		throw new EntDataTypeException("equals() for PTFreeRecord not yet implemented.");
    }

    public String toString() {
        return fields.toString();
    }
}
