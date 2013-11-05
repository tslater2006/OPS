package com.enterrupt.runtime;

public enum ExitCode {

	SUCCESS(0),
	GENERIC_FAILURE(1),
	REFLECT_FAIL_RTE_STATIC_INIT(2);

	private int code;

	private ExitCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}
}
