package com.enterrupt;

import java.util.HashMap;
import java.util.Map;

class BuildAssistant {

	public static HashMap<String, Boolean> allRecordFields;
	public static HashMap<String, Page> pageDefnCache;

	public static void init() {
		allRecordFields = new HashMap<String, Boolean>();
		pageDefnCache = new HashMap<String, Page>();
	}

	public static void addRecordField(String recField) {
		allRecordFields.put(recField, true);
	}

	public static void cachePage(Page p) {
		pageDefnCache.put(p.PNLNAME, p);
		//System.out.println("Cached Page." + p.PNLNAME);
	}

	public static void printInfo() {
		for(Map.Entry<String, Boolean> cursor : allRecordFields.entrySet()) {
			//System.out.println("Field: " + cursor.getKey());
		}

		System.out.println("Total pages: " + pageDefnCache.size());
		System.out.println("Total fields: " + allRecordFields.size());
		System.out.println("SQL Stmts Emitted: " + StmtLibrary.emittedStmts.size());
	}
}
