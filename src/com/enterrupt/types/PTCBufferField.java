package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;

public class PTCBufferField extends PTField implements PTDataType {

	public Pointer valuePtr;

	public PTCBufferField() {
		/**
		 * TODO: Determine type based on field metadata.
		 */
		this.valuePtr = new StdPointer(new PTString(), EnumSet.of(MFlag.STRING));
	}

	public Pointer access(String s) {
		throw new EntDataTypeException("Need to implement access() for PTCBufferField.");
	}

    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof PTCBufferField))
            return false;

        PTCBufferField other = (PTCBufferField)obj;
        throw new EntDataTypeException("equals() for PTCBufferField not yet implemened.");
    }

    public String toString() {
        return "PTCBufferField:<>";
    }
}
