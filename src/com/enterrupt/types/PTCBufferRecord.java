package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;

public class PTCBufferRecord extends PTRecord implements PTDataType {

	public PTCBufferRecord(Record r) {
		super(r);

		for(Map.Entry<String, RecordField> cursor : r.fieldTable.entrySet()) {
			this.fields.put(cursor.getKey(), new CBufferPointer(
				new PTCBufferField()));
		}
	}

	public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof PTCBufferRecord))
            return false;

        PTCBufferRecord other = (PTCBufferRecord)obj;
		throw new EntDataTypeException("equals() for PTCBufferRecord not yet implemented.");
    }

    public String toString() {
        return fields.toString();
    }
}
