package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;

public class PTField extends PTObjectType {

	private PTPrimitiveType value;

	protected PTField() {
		super(Type.FIELD);
	}

	protected PTField(RecordField recFieldDefn) {
		super(Type.FIELD);
		/**
		 * TODO: Determine type based on field metadata.
		 */
		this.value = (PTPrimitiveType)PTType.getSentinel(Type.STRING).alloc();
	}

	public PTType dot(String s) {
		throw new EntDataTypeException("Need to implement dot() for PTField.");
	}

	/**
	 * Primitive assignments to a Field should assign to
	 * the field's underlying value; i.e., SSR_STDNT_TERM0.EMPLID = %EmployeeId
	 */
	public void assgmtDelegate(PTPrimitiveType src) {
		this.value.copyValueFrom(src);
	}

	public boolean typeCheck(PTType a) {
		return (a instanceof PTField &&
			this.getType() == a.getType());
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(super.toString());
		b.append(",value=").append(value.toString());
		return b.toString();
	}
}
