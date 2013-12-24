package com.enterrupt.types;

import java.util.EnumSet;

public class PTString extends PTPrimitive<String> {

	private String s;

	public PTString() {
		this.s = null;
	}

	public PTString(String initial) {
		this.s = initial;
	}

	public String value() {
		return this.s;
	}

	public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof PTString))
            return false;

        PTString other = (PTString)obj;
        if(this.value().equals(other.value())) {
            return true;
        }
        return false;
    }

	public String toString() {
		return "\"" + this.s + "\"";
	}
}
