package com.enterrupt.pt;

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
	public static HashMap<String, String> defnLiteralReservedWordsTable;

	private static HashMap<String, Boolean> systemRecords;

	/**
	 * IMPORTANT: Keep this list synchronized with the one in
	 * PeopleCode.g4.
	 */
	private static final String[] defnLiteralReservedWords = new String[]{
		"BarName",
		"BusActivity",
		"BusEvent",
		"BusProcess",
		"CompIntfc",
		"Component",
		"Field",
		"FileLayout",
		"HTML",
		"Image",
		"Interlink",
		"ItemName",
		"MenuName",
		"Message",
		"Operation",
		"Page",
		"Panel",
		"PanelGroup",
		"RecName",
		"Record",
		"Scroll",
		"SQL",
		"StyleSheet"
	};

	static {
		systemRecords = new HashMap<String, Boolean>();
		systemRecords.put("PSXLATITEM", true);

		defnLiteralReservedWordsTable = new HashMap<String, String>();
		for(String s : defnLiteralReservedWords) {
            defnLiteralReservedWordsTable.put(s.toUpperCase(), s);
        }

	}

	public static boolean isSystemRecord(String RECNAME) {
		return systemRecords.get(RECNAME) != null;
	}
}

