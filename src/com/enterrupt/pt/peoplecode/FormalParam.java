package com.enterrupt.pt.peoplecode;

import com.enterrupt.types.*;

public class FormalParam {

	public PTType type;
    public String id;

    public FormalParam(PTType t, String i) {
    	this.type = t;
        this.id = i;
    }

	public String toString() {
		StringBuilder b = new StringBuilder(this.id);
		b.append(":").append(this.type);
		return b.toString();
	}
}

