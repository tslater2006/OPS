package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;

public class PTField implements PTDataType {

	public PTField() {}

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

	public String toString() {
		return "";
	}
}
