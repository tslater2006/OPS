package com.enterrupt.antlr4;

import com.enterrupt.runtime.EntVMachRuntimeException;

public class EntReturnException extends EntVMachRuntimeException {

	public EntReturnException(String instructionText) {
		super(instructionText);
	}
}
