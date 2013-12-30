package com.enterrupt.types;

import java.util.EnumSet;

public class PTString extends PTPrimitiveType<String> {

	private String s;

	protected PTString() {
		super(Type.STRING);
	}

	public String read() {
		return this.s;
	}

	public void write(String newValue) {
		this.checkIsWriteable();
		this.s = newValue;
	}

	public void systemWrite(String newValue) {
		this.checkIsSystemWriteable();
		this.s = newValue;
	}

	public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof PTString))
            return false;

        PTString other = (PTString)obj;
        if(this.read().equals(other.read())) {
            return true;
        }
        return false;
    }

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(super.toString());
		b.append(",s=").append(s);
		return b.toString();
	}
}
