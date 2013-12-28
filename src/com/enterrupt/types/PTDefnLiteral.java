package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;
import com.enterrupt.runtime.*;

public class PTDefnLiteral implements PTDataType {

	public PTDefnLiteral() {}

	/**
	 * Accesses on a defn literal reserved word always
	 * resolve to the string itself; i.e., Menu.SA_LEARNER_SERVICES
	 * resolves to "SA_LEARNER_SERVICES".
	 */
	public Pointer access(String s) {
		return Environment.getFromLiteralPool(s);
	}

	public boolean equals(Object obj) {
		throw new EntDataTypeException("Encountered illegal equals() call on "
			+ "a PTDefnLiteral object.");
    }

	public String toString() {
		return "#PTDefnLiteral";
	}
}
