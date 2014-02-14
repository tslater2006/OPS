package com.enterrupt.runtime;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.apache.logging.log4j.*;
import com.enterrupt.sql.*;
import com.enterrupt.trace.*;

public class TraceFileVerifier {

	private static List<IEmission> unenforcedEmissions;
	private static Map<String, Boolean> ignoredStmts;
	private static String currTraceLine = "";
	private static int currTraceLineNbr = 0;
	private static BufferedReader traceFileReader;
	private static Pattern sqlTokenPattern, bindValPattern, pcStartPattern,
		pcBeginPattern, pcInstrPattern, pcEndPattern;

	private static int coverageAreaStartLineNbr, coverageAreaEndLineNbr;
	private static int numEnforcedSQLEmissions, numPCEmissionMatches;
	private static int numTraceSQLStmts, numTraceSQLStmtsIgnored;

	private static Logger log = LogManager.getLogger(TraceFileVerifier.class.getName());

	static {
		unenforcedEmissions = new ArrayList<IEmission>();
		ignoredStmts = new HashMap<String, Boolean>();
		sqlTokenPattern = Pattern.compile("\\sStmt=(.*)");
		bindValPattern = Pattern.compile("\\sBind-(\\d+)\\stype=\\d+\\slength=\\d+\\svalue=(.*)");
		pcStartPattern = Pattern.compile("\\s+>>>\\s+(start|start-ext)\\s+Nest=(\\d+)\\s+([A-Za-z_0-9]*?)\\s+([A-Za-z\\._0-9]+)");
		pcBeginPattern = Pattern.compile("\\s>>>>>\\s+Begin\\s+([A-Za-z\\._0-9]+)\\s+level\\s+(\\d+)\\s+row\\s+(\\d+)");
		pcEndPattern = Pattern.compile("\\s+<<<\\s(end|end-ext)\\s+Nest=(\\d+)\\s+([A-Za-z0-9]*?)\\s+([A-Za-z\\._0-9]+)");

		// Note: this pattern excludes any and all trailing semicolons.
		pcInstrPattern = Pattern.compile("\\s+\\d+:\\s+(.+?)[;]*$");

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

		ignoreStmtsInFile(System.getProperty("ignore_stmts_file"));
		ignoreStmtsInFile(System.getProperty("defn_stmts_file"));
	}

	public static void ignoreStmtsInFile(String filename) {
		try {
			BufferedReader ignoreFileReader = new BufferedReader(new FileReader(
				new File(filename)));

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

	public static void submitUnenforcedEmission(IEmission evmEmission) {
		//log.debug(evmEmission);
		unenforcedEmissions.add(evmEmission);
	}

	public static void enforceEmission(IEmission evmEmission) {
		if(!(evmEmission instanceof ENTStmt)) {
			log.debug(evmEmission);
		}

		IEmission traceEmission;
		if(coverageAreaStartLineNbr == 0) {
			/**
			 * If this is the first emission being matched, seek
			 * to the match in the trace file. It is assumed that the first
			 * EVM emission is a SQL statement due to the need to get component
			 * metadata; if this routine is used to check tracefiles that have been
			 * generated with a non-empty cache, this assumption may not be valid.
			 */
			do {
				traceEmission = getNextTraceEmission();
			} while(traceEmission != null &&
					!evmEmission.equals(traceEmission));

			if(traceEmission != null) {
				coverageAreaStartLineNbr = currTraceLineNbr;
				numEnforcedSQLEmissions++;
				return;
			}
		} else {
			traceEmission = getNextTraceEmission();
		}

		if(traceEmission == null) {
			throw new EntVMachRuntimeException("Reached EOF of trace file; no " +
				"emission match is possible.");
		}

		if(evmEmission.equals(traceEmission)) {

			// Increment emission-specific counter.
			if(evmEmission instanceof ENTStmt) {
				numEnforcedSQLEmissions++;
			} else {
				numPCEmissionMatches++;
			}
		} else {
			log.fatal("=== Emission Mismatch! =======================");
			log.fatal("EVM emitted: {}", evmEmission);
			log.fatal("Trace file expects: {}", traceEmission);
			throw new EntVMachRuntimeException("Emission mismatch.");
		}
	}

	private static IEmission getNextTraceEmission() {

		// do-while because we want to check the line returned from the prior call
		// to getNextTraceLine().
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

			Matcher pcStartMatcher = pcStartPattern.matcher(currTraceLine);
			if(pcStartMatcher.find()) {
				// We don't want the next call to check this line again.
				currTraceLine = getNextTraceLine();
				return new PCStart(pcStartMatcher.group(1), pcStartMatcher.group(2),
					pcStartMatcher.group(3), pcStartMatcher.group(4));
			}

			Matcher pcBeginMatcher = pcBeginPattern.matcher(currTraceLine);
			if(pcBeginMatcher.find()) {
				// We don't want the next call to check this line again.
				currTraceLine = getNextTraceLine();
				return new PCBegin(pcBeginMatcher.group(1), pcBeginMatcher.group(2),
							pcBeginMatcher.group(3));
			}

			Matcher pcInstrMatcher = pcInstrPattern.matcher(currTraceLine);
			if(pcInstrMatcher.find()) {
				// We don't want the next call to check this line again.
				currTraceLine = getNextTraceLine();
				return new PCInstruction(pcInstrMatcher.group(1));
			}

			Matcher pcEndMatcher = pcEndPattern.matcher(currTraceLine);
			if(pcEndMatcher.find()) {
				// We don't want the next call to check this line again.
				currTraceLine = getNextTraceLine();
				return new PCEnd(pcEndMatcher.group(1), pcEndMatcher.group(2),
							pcEndMatcher.group(3), pcEndMatcher.group(4));
			}
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
		log.info("Adhoc SQL Emissions:\t\t\t{}", unenforcedEmissions.size());
		log.info("Enforced SQL Emissions:\t\t\t{}", numEnforcedSQLEmissions);
		log.info("Enforced PC Emissions:\t\t\t{}", numPCEmissionMatches);
		log.info("Total Emissions:\t\t\t\t{}",
			numEnforcedSQLEmissions + unenforcedEmissions.size() + numPCEmissionMatches);
		log.info("Component Structure Valid?\t\t\t\t{}",
			ComponentStructureVerifier.hasBeenVerified ? "YES" : "!!NO!!");
		log.info("Coverage Area Bounded?\t\t\t\t{}",
			mismatchFlag ? "!!NO!!" : "YES");
		log.info("Coverage Area (Start / End Lines):\tL_{}\t\tL_{}",
			coverageAreaStartLineNbr, coverageAreaEndLineNbr);
		log.info("\nNext unmatched emission in trace file: {}", getNextTraceEmission());
	}
}
