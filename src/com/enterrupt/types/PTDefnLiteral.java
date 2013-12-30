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

	public void assgmtDelegate(PTPrimitiveType src) {
		throw new EntDataTypeException("assgmtDelegate for defn literal objects " +
			"is not yet supported.");
	}

	public boolean typeCheck(PTType a) {
		return (a instanceof PTDefnLiteral &&
			this.getType() == a.getType());
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
