package com.enterrupt.types;

import java.util.EnumSet;

public class PTBoolean extends PTPrimitive<Boolean> {

	private Boolean b;

	public PTBoolean() {
		this.b = false;
	}

	public PTBoolean(Boolean initial) {
		this.b = initial;
	}

	public Boolean value() {
		return this.b;
	}

    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof PTBoolean))
            return false;

        PTBoolean other = (PTBoolean)obj;
		if(this.value().equals(other.value())) {
            return true;
        }
        return false;
    }

	public String toString() {
		if(this.b) { return "True"; }
		return "False";
	}
}

