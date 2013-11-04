package com.enterrupt.pt_objects;

import java.util.HashMap;

public class PSDefn {

	public static final String RECORD = "1";
	public static final String FIELD = "2";
	public static final String COMPONENT = "10";
	public static final String EVENT = "12";
	public static final String MARKET = "39";
	public static final String APP_PACKAGE = "104";
	public static final String PAGE = "9";
	public static final String NULL = " ";

	private static HashMap<String, Boolean> systemRecords;

	static {
		systemRecords = new HashMap<String, Boolean>();
		systemRecords.put("PSXLATITEM", true);
	}

	public static boolean isSystemRecord(String RECNAME) {
		return systemRecords.get(RECNAME) != null;
	}
}

