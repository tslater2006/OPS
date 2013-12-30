package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;

public class PTField extends PTObjectType {

	private PTType value;

	protected PTField(RecordField recFieldDefn) {
		super(Type.FIELD);
		/**
		 * TODO: Determine type based on field metadata.
		 */
		this.value = PTType.getSentinel(Type.STRING).alloc();
	}

	public PTType dot(String s) {
		throw new EntDataTypeException("Need to implement dot() for PTFreeField.");
	}
}
