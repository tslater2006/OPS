package com.enterrupt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.regex.Matcher;
import java.text.DecimalFormat;
import java.util.regex.Pattern;
import java.util.HashMap;
import com.enterrupt.buffers.ComponentBuffer;
import com.enterrupt.pt_objects.*;
import com.enterrupt.sql.*;

public class BuildAssistant {

	private static final String SQL_TOKEN_REGEX = "\\sStmt=(.*)";
	private static final String BIND_VAL_REGEX = "\\sBind-(\\d+)\\stype=\\d+\\slength=\\d+\\svalue=(.*)";
	private static BufferedReader traceReader;
	private static String currentTraceLine = "";
	private static HashMap<String, Boolean> ignoredStmts;
	private static int currTraceLineNbr = 0;

	public static void runValidationTests(Component componentObj) throws Exception {
		openTraceFile();
		loadIgnoredStmts();

		PSStmt ps_stmt;

		int totalEmittedStmts = StmtLibrary.emittedStmts.size();
		int curr_ent_stmt_idx = 0;
		int max_idx = totalEmittedStmts - 1;

		boolean inCoverageRegion = false;
		int coverageStartLine = -1, coverageEndLine = -1;

		int curr_unmatched_idx = 0, unmatched_size = 10;
		int[] firstUnmatchedTokenLineNbrs = new int[unmatched_size];

		double numTraceStmts = 0.0;
		double numIgnoredTraceStmts = 0.0;
		double numCoverageAreaStmts = 0.0;
		double numIgnoredCoverageAreaStmts = 0.0;

		double numCoverageAreaMatches = 0.0;

		// Find match for first emitted ENT stmt.
		while((ps_stmt = getNextSqlStmt()) != null) {
			numTraceStmts++;

			/*if(ps_stmt.line_nbr == 1200) {
				System.out.println(ps_stmt);
				System.out.println(StmtLibrary.emittedStmts.get(curr_ent_stmt_idx));
			}*/

			if(inCoverageRegion) {
				numCoverageAreaStmts++;
			}

			if(ignoredStmts.containsKey(ps_stmt.originalStmt)) {
				numIgnoredTraceStmts++;
				if(inCoverageRegion) {
					numIgnoredCoverageAreaStmts++;
				}
				continue;
			}

			if(curr_ent_stmt_idx <= max_idx) {
				if(ps_stmt.equals(StmtLibrary.emittedStmts.get(curr_ent_stmt_idx))) {
					if(curr_ent_stmt_idx == 0) {
						inCoverageRegion = true;
						coverageStartLine = ps_stmt.line_nbr;
						numCoverageAreaStmts = 1;
					}

					numCoverageAreaMatches++;

					if(curr_ent_stmt_idx == max_idx) {
						inCoverageRegion = false;
						coverageEndLine = ps_stmt.line_nbr;
					}

					curr_ent_stmt_idx++;
				} else {
					if(inCoverageRegion && (curr_unmatched_idx < unmatched_size)) {
						firstUnmatchedTokenLineNbrs[curr_unmatched_idx] = ps_stmt.line_nbr;
						curr_unmatched_idx++;
					}
				}
			}
		}

		boolean isCompStructureValid = componentObj.validateComponentStructure(false);

		DecimalFormat df = new DecimalFormat("0.0");

		System.out.println("\nTRACE FILE VERIFICATION SUMMARY");
		System.out.println("=======================================================================================");
		System.out.println("Stmts in Ignore File:\t\t\t\t" + ignoredStmts.size());
		System.out.println("Lines in trace file:\t\t\t\t" + (currTraceLineNbr - 1));
		System.out.println("Total Trace Stmts / Total Ignored:\t\t" + (int)numTraceStmts +
			"\t\t" + (int)numIgnoredTraceStmts);
		System.out.println("Coverage Stmts / Coverage Stmts Ignored:\t" + (int)numCoverageAreaStmts +
			"\t\t" + (int)numIgnoredCoverageAreaStmts);
		System.out.println("Is Component Structure Valid?\t\t\t\t\t\t\t" +
			(isCompStructureValid ? "YES" : "!!NO!!"));
		System.out.println("Coverage Area Bounded?\t\t\t\t\t\t\t\t" + (inCoverageRegion ? "!!NO!!" : "YES"));
		System.out.println("Coverage Area Start / End Line:\t\t\t" + "L_" + coverageStartLine + "\t\t" +
			"L_" + coverageEndLine);
		System.out.println("Matches / Total Emitted:\t\t\t" + (int)numCoverageAreaMatches + "\t\t"
			+ totalEmittedStmts + "\t\t" + df.format((numCoverageAreaMatches / totalEmittedStmts) * 100.0) + "%");
		System.out.println("% ENT Coverage of Area (- ignored):\t\t\t\t\t\t" +
			df.format((numCoverageAreaMatches / (numCoverageAreaStmts - numIgnoredCoverageAreaStmts)) * 100.0) + "%");
		System.out.println("% ENT Coverage of File (- ignored):\t\t\t\t\t\t" +
			df.format((numCoverageAreaMatches / (numTraceStmts - numIgnoredTraceStmts)) * 100.0) + "%");
		System.out.print("First Unmatched Coverage Area Line Nbrs:\t" + firstUnmatchedTokenLineNbrs[0]);
		for(int i=1; i < unmatched_size; i++) {
			System.out.print(", " + firstUnmatchedTokenLineNbrs[i]);
		}
		System.out.println("\n");

		closeTraceFile();
	}

	public static void loadIgnoredStmts() {
		try {
			String line = null;
			File ignore_file = new File(System.getProperty("ignore_stmts_file"));
			BufferedReader ignoreReader = new BufferedReader(new FileReader(ignore_file));

			ignoredStmts = new HashMap<String, Boolean>();
			while((line = ignoreReader.readLine()) != null) {
				ignoredStmts.put(line, true);
			}
			ignoreReader.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void openTraceFile() {
		try {
			File trace_file = new File(System.getProperty("tracefile"));
			traceReader = new BufferedReader(new FileReader(trace_file));
			currTraceLineNbr = 0;
		} catch(Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	public static PSStmt getNextSqlStmt() {
		PSStmt ps_stmt = null;
		Pattern sqlTokenPattern = Pattern.compile(SQL_TOKEN_REGEX);
		Pattern bindValPattern = Pattern.compile(BIND_VAL_REGEX);

		do {

			Matcher sqlMatcher = sqlTokenPattern.matcher(currentTraceLine);
			if(sqlMatcher.find()) {
				ps_stmt = new PSStmt(sqlMatcher.group(1), currTraceLineNbr);

				// Keep reading lines for bind values.
				while((currentTraceLine = getNextTraceLine()) != null) {
					Matcher bindValMatcher = bindValPattern.matcher(currentTraceLine);
					if(bindValMatcher.find()) {
						ps_stmt.bindVals.put(Integer.parseInt(bindValMatcher.group(1)),
							bindValMatcher.group(2));
					} else {
						return ps_stmt; // statement has one or more bind values.
					}
				}

				return ps_stmt; // statement has no bind values.
			}
		} while((currentTraceLine = getNextTraceLine()) != null);

		return ps_stmt;
	}

	public static String getNextTraceLine() {
		String line = null;
		try {
			line = traceReader.readLine();
			currTraceLineNbr++;
		} catch(Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		return line;
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
