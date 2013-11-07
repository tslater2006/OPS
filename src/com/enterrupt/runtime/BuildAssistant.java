package com.enterrupt.runtime;

import java.util.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.regex.*;
import com.enterrupt.buffers.*;
import com.enterrupt.pt.*;
import com.enterrupt.sql.*;
import com.enterrupt.runtime.*;
import org.apache.logging.log4j.*;

public class BuildAssistant {

	private static final String SQL_TOKEN_REGEX = "\\sStmt=(.*)";
	private static final String BIND_VAL_REGEX = "\\sBind-(\\d+)\\stype=\\d+\\slength=\\d+\\svalue=(.*)";
	private static BufferedReader traceReader;
	private static String currentTraceLine = "";
	private static HashMap<String, Boolean> ignoredStmts;
	private static int currTraceLineNbr = 0;

	private static Logger log = LogManager.getLogger(BuildAssistant.class.getName());

	public static boolean validateComponentStructure(Component componentObj, boolean verboseFlag) {

	    int indent = 0;
        IStreamableBuffer buf;

        File structureFile = new File("test/" + componentObj.PNLGRPNAME + ".structure");
		BufferedReader reader = null;

		try {
	        reader = new BufferedReader(new FileReader(structureFile));
		} catch(java.io.FileNotFoundException fnfe) {
			log.fatal(fnfe.getMessage(), fnfe);
			System.exit(ExitCode.COMP_STRUCTURE_FILE_NOT_FOUND.getCode());
		}

        String line = null;
        String lineParts[];

        ComponentBuffer.resetCursors();
        while((buf = ComponentBuffer.next()) != null) {

			try {
	            line = reader.readLine().trim();
			} catch(java.io.IOException ioe) {
				log.fatal(ioe.getMessage(), ioe);
				System.exit(ExitCode.FAILED_READ_FROM_COMP_STRUCT_FILE.getCode());
			}

            lineParts = line.split(";");

            if(buf instanceof ScrollBuffer) {

                ScrollBuffer sbuf = (ScrollBuffer) buf;
                indent = sbuf.scrollLevel * 3;

                if(verboseFlag) {
					StringBuilder b = new StringBuilder();
                    for(int i=0; i<indent; i++){b.append(" ");}
                    b.append("Scroll - Level ").append(sbuf.scrollLevel).append("\tPrimary Record: ")
                        .append(sbuf.primaryRecName);
                    for(int i=0; i<indent; i++){b.append(" ");}
					log.info(b.toString());
                    log.info("=======================================================");
                }

                if(lineParts.length != 3 || !lineParts[0].equals("SCROLL") ||
                    Integer.parseInt(lineParts[1]) != sbuf.scrollLevel ||
                        (!lineParts[2].replaceAll("-", "_").equals(sbuf.primaryRecName)
                            && Integer.parseInt(lineParts[1]) > 0)) {
                    throw new EntVMachRuntimeException("Incorrect/absent scroll token encountered " +
						"during component structure validation.");
                }

            } else if(buf instanceof RecordBuffer) {
                RecordBuffer rbuf = (RecordBuffer) buf;

                if(verboseFlag) {
					StringBuilder b = new StringBuilder();
                    for(int i=0; i<indent; i++){b.append(" ");}
                   	b.append(" + ").append(rbuf.recName);
					log.info(b.toString());
                }

                if(lineParts.length != 2 || !lineParts[0].equals("RECORD") ||
                    !lineParts[1].replaceAll("-", "_").equals(rbuf.recName)) {
                    throw new EntVMachRuntimeException("Incorrect/absent record token encountered " +
						"during component structure validation.");
                }

            } else {
                RecordFieldBuffer fbuf = (RecordFieldBuffer) buf;

               if(verboseFlag) {
					StringBuilder b = new StringBuilder();
                    for(int i=0; i<indent; i++){b.append(" ");}
                    b.append("   - ").append(fbuf.fldName);
					log.info(b.toString());
                }

                if(lineParts.length != 2 || !lineParts[0].equals("FIELD") ||
                    !lineParts[1].replaceAll("-", "_").equals(fbuf.fldName)) {
                    throw new EntVMachRuntimeException("Incorrect/absent field token encountered " +
						"during component structure validation.");
                }
            }
        }

		try {
	        if(!reader.readLine().trim().equals("END-COMPONENT-STRUCTURE")) {
   	        	throw new EntVMachRuntimeException("Expected END-COMPONENT-STRUCTURE in .structure file.");
        	}
		} catch(java.io.IOException ioe) {
            log.fatal(ioe.getMessage(), ioe);
        	System.exit(ExitCode.FAILED_READ_FROM_COMP_STRUCT_FILE.getCode());
        }

        return true;
	}

	public static void runValidationTests(Component componentObj) {
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

					//log.debug(StmtLibrary.emittedStmts.get(curr_ent_stmt_idx));

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

		boolean isCompStructureValid = validateComponentStructure(componentObj, false);

		DecimalFormat df = new DecimalFormat("0.0");

		log.info("============================================================================");
		log.info("TRACE FILE VERIFICATION SUMMARY");
		log.info("============================================================================");
		log.info("Stmts in Ignore File:\t\t\t" + ignoredStmts.size());
		log.info("Lines in trace file:\t\t\t" + (currTraceLineNbr - 1));
		log.info("Total Trace Stmts / Total Ignored:\t\t" + (int)numTraceStmts +
			"\t\t" + (int)numIgnoredTraceStmts);
		log.info("Coverage Stmts / Coverage Stmts Ignored:\t" + (int)numCoverageAreaStmts +
			"\t\t" + (int)numIgnoredCoverageAreaStmts);
		log.info("Is Component Structure Valid?\t\t\t\t\t\t" +
			(isCompStructureValid ? "YES" : "!!NO!!"));
		log.info("Coverage Area Bounded?\t\t\t\t\t\t\t" + (inCoverageRegion ? "!!NO!!" : "YES"));
		log.info("Coverage Area Start / End Line:\t\t" + "L_" + coverageStartLine + "\t\t" +
			"L_" + coverageEndLine);
		log.info("Matches / Total Emitted:\t\t\t" + (int)numCoverageAreaMatches + "\t\t"
			+ totalEmittedStmts + "\t\t" + df.format((numCoverageAreaMatches / totalEmittedStmts) * 100.0) + "%");
		log.info("% ENT Coverage of Area (- ignored):\t\t\t\t\t" +
			df.format((numCoverageAreaMatches / (numCoverageAreaStmts - numIgnoredCoverageAreaStmts)) * 100.0) + "%");
		log.info("% ENT Coverage of File (- ignored):\t\t\t\t\t" +
			df.format((numCoverageAreaMatches / (numTraceStmts - numIgnoredTraceStmts)) * 100.0) + "%");

		StringBuilder b = new StringBuilder();
		b.append("First Unmatched Coverage Area Line Nbrs:\t").append(firstUnmatchedTokenLineNbrs[0]);
		for(int i=1; i < unmatched_size; i++) {
			b.append(", ").append(firstUnmatchedTokenLineNbrs[i]);
		}
		log.info(b.toString());

		/*if(curr_unmatched_idx < StmtLibrary.emittedStmts.size()) {
			b = new StringBuilder();
			b.append("First Unmatched ENT Stmt:\n");
			b.append(StmtLibrary.emittedStmts.get(curr_unmatched_idx));
			log.info(b.toString());
		}*/

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
		} catch(java.io.FileNotFoundException fnfe) {
			log.fatal(fnfe.getMessage(), fnfe);
			System.exit(ExitCode.IGNORE_STMTS_FILE_NOT_FOUND.getCode());
		} catch(java.io.IOException ioe) {
			log.fatal(ioe.getMessage(), ioe);
			System.exit(ExitCode.FAILED_READ_FROM_IGNORE_STMTS_FILE.getCode());
		}
	}

	public static void openTraceFile() {
		try {
			File trace_file = new File(System.getProperty("tracefile"));
			traceReader = new BufferedReader(new FileReader(trace_file));
			currTraceLineNbr = 0;
        } catch(java.io.FileNotFoundException fnfe) {
            log.fatal(fnfe.getMessage(), fnfe);
            System.exit(ExitCode.TRACE_FILE_NOT_FOUND.getCode());
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
        } catch(java.io.IOException ioe) {
            log.fatal(ioe.getMessage(), ioe);
            System.exit(ExitCode.FAILED_READ_FROM_TRACE_FILE.getCode());
        }

		return line;
	}

	public static void closeTraceFile() {

		try {
			traceReader.close();
		} catch(java.io.IOException ioe) {
			log.warn("Encountered IOException while attempting to close trace file.");
		}
	}

	public static void printComponentStructure() {

	    int indent = 0;
        IStreamableBuffer buf;

        ComponentBuffer.resetCursors();
        while((buf = ComponentBuffer.next()) != null) {

            if(buf instanceof ScrollBuffer) {

                ScrollBuffer sbuf = (ScrollBuffer) buf;
                indent = sbuf.scrollLevel * 3;

				StringBuilder b = new StringBuilder();
                for(int i=0; i<indent; i++){b.append(" ");}
                b.append("Scroll - Level ").append(sbuf.scrollLevel).append("\tPrimary Record: ")
                      .append(sbuf.primaryRecName);
                for(int i=0; i<indent; i++){b.append(" ");}
				log.info(b.toString());
                log.info("=======================================================");

            } else if(buf instanceof RecordBuffer) {

                RecordBuffer rbuf = (RecordBuffer) buf;

				StringBuilder b = new StringBuilder();
                for(int i=0; i<indent; i++){b.append(" ");}
               	b.append(" + ").append(rbuf.recName);
				log.info(b.toString());

            } else {
                RecordFieldBuffer fbuf = (RecordFieldBuffer) buf;

				StringBuilder b = new StringBuilder();
                for(int i=0; i<indent; i++){b.append(" ");}
                b.append("   - ").append(fbuf.fldName);
				log.info(b.toString());
            }
        }
	}
}
