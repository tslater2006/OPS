package com.enterrupt.interpreter;

import com.enterrupt.runtime.EntVMachRuntimeException;

public class EntInterpretException extends EntVMachRuntimeException {

	public EntInterpretException(String msg) {
		super(msg);
	}
}
