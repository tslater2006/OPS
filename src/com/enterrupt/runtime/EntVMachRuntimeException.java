package com.enterrupt.runtime;

import java.lang.RuntimeException;

public class EntVMachRuntimeException extends RuntimeException {

	public EntVMachRuntimeException(String msg) {
		super(msg);
	}

	public EntVMachRuntimeException(String msg, Exception ex) {
		super(msg, ex);
	}
}
