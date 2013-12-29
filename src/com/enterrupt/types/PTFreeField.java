package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;
import com.enterrupt.memory.*;

public class PTFreeField extends PTField implements PTDataType {

	public PTFreeField() {
		/**
		 * TODO: Determine type based on field metadata.
		 */
		this.valuePtr = new StdPointer(new PTString(), EnumSet.of(MFlag.STRING));
	}

	public Pointer access(String s) {
		throw new EntDataTypeException("Need to implement access() for PTFreeField.");
	}

    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof PTFreeField))
            return false;

        PTFreeField other = (PTFreeField)obj;
        throw new EntDataTypeException("equals() for PTFreeField not yet implemented.");
    }

    public String toString() {
        return "PTFreeField:<>";
    }
}
