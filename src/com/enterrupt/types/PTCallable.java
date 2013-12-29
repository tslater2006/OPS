package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;
import com.enterrupt.runtime.*;
import com.enterrupt.memory.*;

public class PTCallable implements PTDataType {

	public ExecContext eCtx;

	public PTCallable(ExecContext e) {
		this.eCtx = e;
	}

	public Pointer access(String s) {
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
