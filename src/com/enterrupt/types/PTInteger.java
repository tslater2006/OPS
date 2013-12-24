package com.enterrupt.types;

import java.util.EnumSet;

public class PTInteger extends PTPrimitive<Integer> {

	private Integer i;

	public PTInteger() {
		this.i = 0;
	}

	public PTInteger(Integer initial) {
		this.i = initial;
	}

	public Integer value() {
		return this.i;
	}

	public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof PTInteger))
            return false;

        PTInteger other = (PTInteger)obj;
        if(this.value().equals(other.value())) {
            return true;
        }
        return false;
    }

	public String toString() {
		return i.toString();
	}
}
