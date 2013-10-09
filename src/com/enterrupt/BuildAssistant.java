package com.enterrupt;

import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BuildAssistant {

	public static HashMap<String, Boolean> allRecordFields;
	public static HashMap<String, Page> pageDefnCache;
	private static final String SQL_TOKEN_REGEX = "\\sStmt=(.*)";
	private static final String BIND_VAL_REGEX = "\\sBind-(\\d+)\\stype=\\d+\\slength=\\d+\\svalue=(.*)";
	private static BufferedReader traceReader;

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

	public static void verifyAgainstTraceFile() {
		openTraceFile();

		PSStmt ps_stmt;
		while((ps_stmt = getNextSqlStmt()) != null) {
			System.out.println(ps_stmt.bindVals.size());
		}

		closeTraceFile();
	}

	public static void openTraceFile() {
		try {
			File trace_file = new File(System.getProperty("tracefile"));
			traceReader = new BufferedReader(new FileReader(trace_file));
		} catch(Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	public static PSStmt getNextSqlStmt() {
		PSStmt ps_stmt = null;
		Pattern sqlTokenPattern = Pattern.compile(SQL_TOKEN_REGEX);
		Pattern bindValPattern = Pattern.compile(BIND_VAL_REGEX);

		try {
			String line = "";
			while((line = traceReader.readLine()) != null) {
				Matcher sqlMatcher = sqlTokenPattern.matcher(line);
				if(sqlMatcher.find()) {
					ps_stmt = new PSStmt(sqlMatcher.group(1));

					// Keep reading lines for bind values.
					while((line = traceReader.readLine()) != null) {
						Matcher bindValMatcher = bindValPattern.matcher(line);
						if(bindValMatcher.find()) {
							ps_stmt.bindVals.put(Integer.parseInt(bindValMatcher.group(1)),
								bindValMatcher.group(2));
						} else {
							return ps_stmt;
						}
					}
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		return ps_stmt;
	}

	public static void closeTraceFile() {
		try {
			traceReader.close();
		} catch(Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
