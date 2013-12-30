package com.enterrupt.types;

import java.util.EnumSet;
import com.enterrupt.runtime.*;

public class PTString extends PTPrimitiveType<String> {

	private String s;

	protected PTString() {
		super(Type.STRING);
	}

	public String read() {
		return this.s;
	}

	public void write(String newValue) {
        if(this.isSentinel()) {
            throw new EntVMachRuntimeException("Attempted illegal write to a " +
                "sentinel PTType object.");
        }
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

	public String toString() {
		return "\"" + this.s + "\"";
	}
}
