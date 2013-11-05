package com.enterrupt.runtime;

public enum ExitCode {

	SUCCESS(0),
	GENERIC_FAILURE(1),
	REFLECT_FAIL_RTE_STATIC_INIT(2),
	FAILED_TO_CREATE_PSTMT_FROM_CONN(3),
	UNABLE_TO_ACQUIRE_DB_CONN(4);

	private int code;

	private ExitCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}
}
