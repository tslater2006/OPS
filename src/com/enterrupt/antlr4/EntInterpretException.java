package com.enterrupt.antlr4;

import com.enterrupt.runtime.EntVMachRuntimeException;

public class EntInterpretException extends EntVMachRuntimeException {

	public EntInterpretException(String msg) {
		super(msg);
	}
}
