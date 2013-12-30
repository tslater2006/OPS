package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;
import com.enterrupt.runtime.*;

public class PTDefnLiteral extends PTObjectType {

	protected PTDefnLiteral() {
		super(Type.DEFN_LITERAL);
	}

	/**
	 * Accesses on a defn literal reserved word always
	 * resolve to the string itself; i.e., Menu.SA_LEARNER_SERVICES
	 * resolves to "SA_LEARNER_SERVICES".
	 */
	public PTType dot(String s) {
		return Environment.getFromLiteralPool(s);
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
