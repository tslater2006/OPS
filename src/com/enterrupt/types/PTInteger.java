package com.enterrupt.types;

import java.util.EnumSet;

public class PTInteger extends PTPrimitiveType<Integer> {

	private Integer i;

	protected PTInteger() {
		super(Type.INTEGER);
	}

	public Integer read() {
		return this.i;
	}

    public void write(Integer newValue) {
        this.checkIsWriteable();
        this.i = newValue;
    }

    public void systemWrite(Integer newValue) {
        this.checkIsSystemWriteable();
        this.i = newValue;
    }

	public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof PTInteger))
            return false;

        PTInteger other = (PTInteger)obj;
        if(this.read().equals(other.read())) {
            return true;
        }
        return false;
    }

	public String toString() {
		return i.toString();
	}
}
