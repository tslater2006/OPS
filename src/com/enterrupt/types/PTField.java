package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;

public class PTField implements PTDataType {

	public MemPointer valuePtr;

	public PTField() {
		/**
		 * TODO: Determine type based on field metadata.
		 */
		this.valuePtr = new MemPointer(new PTString());
	}

	public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof PTField))
            return false;

        PTField other = (PTField)obj;
		throw new EntDataTypeException("equals() for PTField not yet implemented.");
    }

	public MemPointer access(String s) {
		throw new EntDataTypeException("Need to implement access() for PTField.");
	}

	public String toString() {
		return "";
	}
}
