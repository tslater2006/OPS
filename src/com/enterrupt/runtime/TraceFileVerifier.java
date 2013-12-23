package com.enterrupt.runtime;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.apache.logging.log4j.*;
import com.enterrupt.sql.*;

public class TraceFileVerifier {

	private static List<Object> emissions;
	private static Map<String, Boolean> ignoredStmts;
	private static String currTraceLine = "";
	private static int currTraceLineNbr = 0;
	private static BufferedReader traceFileReader;
	private static Pattern sqlTokenPattern, bindValPattern;

	private static int coverageAreaStartLineNbr, coverageAreaEndLineNbr;
	private static int numEmissionMatches, numSQLEmissionMatches;
	private static int numTraceSQLStmts, numTraceSQLStmtsIgnored;

	private static Logger log = LogManager.getLogger(TraceFileVerifier.class.getName());

	static {
		ignoredStmts = new HashMap<String, Boolean>();
		sqlTokenPattern = Pattern.compile("\\sStmt=(.*)");
		bindValPattern = Pattern.compile("\\sBind-(\\d+)\\stype=\\d+\\slength=\\d+\\svalue=(.*)");

		/**
	 	 * Open trace file for reading.
		 */
		try {
			traceFileReader = new BufferedReader(new FileReader(
				new File(System.getProperty("tracefile"))));
		} catch(java.io.FileNotFoundException fnfe) {
            log.fatal(fnfe.getMessage(), fnfe);
            System.exit(ExitCode.TRACE_FILE_NOT_FOUND.getCode());
        }

		/**
		 * Open ignored stmts file for reading.
		 */
		try {
			BufferedReader ignoreFileReader = new BufferedReader(new FileReader(
				new File(System.getProperty("ignore_stmts_file"))));

			String line;
			while((line = ignoreFileReader.readLine()) != null) {
				ignoredStmts.put(line, true);
			}
			ignoreFileReader.close();
        } catch(java.io.FileNotFoundException fnfe) {
            log.fatal(fnfe.getMessage(), fnfe);
            System.exit(ExitCode.IGNORE_STMTS_FILE_NOT_FOUND.getCode());
        } catch(java.io.IOException ioe) {
            log.fatal(ioe.getMessage(), ioe);
            System.exit(ExitCode.FAILED_READ_FROM_IGNORE_STMTS_FILE.getCode());
        }
	}

	public static void submitEmission(Object evmEmission) {

		Object traceEmission;
		if(coverageAreaStartLineNbr == 0) {
			/**
			 * If this is the first emission being matched, seek
			 * to the match in the trace file.
			 */
			do {
				traceEmission = getNextTraceEmission();
			} while(traceEmission != null &&
					!((ENTStmt)evmEmission).equals((PSStmt)traceEmission));

			if(traceEmission != null) {
				coverageAreaStartLineNbr = currTraceLineNbr;
				numSQLEmissionMatches++;
				numEmissionMatches++;
				return;
			}
		} else {
			traceEmission = getNextTraceEmission();
		}

		if(traceEmission == null) {
			throw new EntVMachRuntimeException("Reached EOF of trace file; no " +
				"emission match is possible.");
		}

		if(evmEmission instanceof ENTStmt &&
			traceEmission instanceof PSStmt) {
				ENTStmt entStmt = (ENTStmt) evmEmission;
				PSStmt psStmt = (PSStmt) traceEmission;

				if(entStmt.equals(psStmt)) {
					numSQLEmissionMatches++;
				} else {
					log.fatal("EVM emitted: {}", entStmt);
					log.fatal("Trace file expects: {}", psStmt);
					throw new EntVMachRuntimeException("SQL emission mismatch.");
				}
		} else {
			throw new EntVMachRuntimeException("Unexpected combination of emission "+
				"types encountered during trace file verification.");
		}

		numEmissionMatches++;
	}

	private static Object getNextTraceEmission() {
		do {
            Matcher sqlMatcher = sqlTokenPattern.matcher(currTraceLine);
            if(sqlMatcher.find()) {
				numTraceSQLStmts++;

                PSStmt ps_stmt = new PSStmt(sqlMatcher.group(1), currTraceLineNbr);

				// If the stmt is in the ignored file, skip it.
				if(ignoredStmts.containsKey(ps_stmt.originalStmt)) {
					numTraceSQLStmtsIgnored++;
					continue;
				}

                // Keep reading lines for bind values.
                while((currTraceLine = getNextTraceLine()) != null) {
                    Matcher bindValMatcher = bindValPattern.matcher(currTraceLine);
                    if(bindValMatcher.find()) {
                        ps_stmt.bindVals.put(Integer.parseInt(bindValMatcher.group(1)),
                            bindValMatcher.group(2));
                    } else {
                        return ps_stmt; // statement has one or more bind values.
                    }
                }

                return ps_stmt; // statement has no bind values.
            }

			/**
			 * TODO: Check for PeopleCode statement execution.
		     */
        } while((currTraceLine = getNextTraceLine()) != null);

        return null;
	}

	private static String getNextTraceLine() {
		String line = null;

       try {
            line = traceFileReader.readLine();
            currTraceLineNbr++;
        } catch(java.io.IOException ioe) {
            log.fatal(ioe.getMessage(), ioe);
            System.exit(ExitCode.FAILED_READ_FROM_TRACE_FILE.getCode());
        }

        return line;
	}

	public static void closeTraceFile() {
    	try {
        	traceFileReader.close();
     	} catch(java.io.IOException ioe) {
        	log.warn("Encountered IOException while attempting to close trace file.");
      	}
	}

	public static void logVerificationSummary(boolean mismatchFlag) {

		if(!mismatchFlag) {
			coverageAreaEndLineNbr = currTraceLineNbr;
		}

		log.info("==============================================================");
		log.info("[{}] Trace File Verification Summary",
			mismatchFlag ? "FAILURE" : "SUCCESS");
		log.info("==============================================================");
		log.info("Trace lines seen:\t\t\t{}", currTraceLineNbr);
		log.info("SQL Stmts in Ignore File:\t\t{}", ignoredStmts.size());
		log.info("SQL Stmts Seen (Total / Ignored):\t{}\t\t{}", numTraceSQLStmts,
			numTraceSQLStmtsIgnored);
		log.info("Matched EVM Emissions:\t\t\t{}", numEmissionMatches);
		log.info("Matched SQL Emissions:\t\t\t{}", numSQLEmissionMatches);
		log.info("Component Structure Valid?\t\t\t\t{}",
			ComponentStructureVerifier.hasBeenVerified ? "YES" : "!!NO!!");
		log.info("Coverage Area Bounded?\t\t\t\t{}",
			mismatchFlag ? "!!NO!!" : "YES");
		log.info("Coverage Area (Start / End Lines):\tL_{}\t\tL_{}",
			coverageAreaStartLineNbr, coverageAreaEndLineNbr);
	}
}
