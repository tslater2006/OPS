package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;
import com.enterrupt.runtime.*;
import com.enterrupt.scope.*;

public class PTCallable implements PTDataType {

	public ExecContext eCtx;

	public PTCallable(ExecContext e) {
		this.eCtx = e;
	}

	public MemPointer access(String s) {
		throw new EntDataTypeException("Encountered illegal access(s) call on "
			+ "a PTCallable object.");
	}

	public boolean equals(Object obj) {
		throw new EntDataTypeException("Encountered illegal equals(obj) call on "
			+ "a PTCallable object.");
    }

	public String toString() {
		return "#PTCallable";
	}
}
