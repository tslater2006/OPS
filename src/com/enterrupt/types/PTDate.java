package com.enterrupt.types;

import java.util.EnumSet;

public class PTDate extends PTPrimitiveType<Void> {

	private Void d;

	protected PTDate() {
		super(Type.DATE);
	}

	public Void read() {
		return this.d;
	}

    public void write(Void newValue) {
        this.checkIsWriteable();
        this.d = newValue;
    }

    public void systemWrite(Void newValue) {
        this.checkIsSystemWriteable();
        this.d = newValue;
    }

	public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof PTDate))
            return false;

        PTDate other = (PTDate)obj;
        if(this.read().equals(other.read())) {
            return true;
        }
        return false;
    }

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(super.toString());
		b.append(",d=").append(this.d);
		return b.toString();
	}
}
