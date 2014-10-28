/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.sql.*;
import org.openpplsoft.trace.*;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Accepts emissions from the OPS runtime and verifies
 * them against a PS tracefile; the exact tracefile is
 * determined by the value of system properties passed
 * to OPS at execution.
 */
public final class TraceFileVerifier {

  private static List<IEmission> unenforcedEmissions;
  private static Map<String, Boolean> ignoredStmts;
  private static String currTraceLine = "";
  private static int currTraceLineNbr;
  private static BufferedReader traceFileReader;
  private static Pattern sqlTokenPattern, bindValPattern, pcStartPattern,
      pcBeginPattern, pcInstrPattern, pcEndPattern, pcRelDispProcStartPattern,
      pcRelDispProcEndPattern, pcFldDefaultPattern;

  private static int coverageAreaStartLineNbr, coverageAreaEndLineNbr;
  private static int numEnforcedSQLEmissions, numPCEmissionMatches;
  private static int numTraceSQLStmts, numTraceSQLStmtsIgnored;
  private static int numOPSOptionalPCInstrsEmitted;

  // Define regex group indices here rather than as magic nbrs.
  private static final int GROUP1 = 1;
  private static final int GROUP2 = 2;
  private static final int GROUP3 = 3;
  private static final int GROUP4 = 4;
  private static final int GROUP5 = 5;

  private static boolean isPausedAndWaitingToSyncUp;
  private static IEmission emissionToSyncUpOn;

  private static Logger log = LogManager.getLogger(
      TraceFileVerifier.class.getName());

  static {
    unenforcedEmissions = new ArrayList<IEmission>();
    ignoredStmts = new HashMap<String, Boolean>();
    sqlTokenPattern = Pattern.compile("\\sStmt=(.*)");
    bindValPattern = Pattern.compile(
        "\\sBind-(\\d+)\\stype=\\d+\\slength=\\d+\\svalue=(.*)");
    pcStartPattern = Pattern.compile(
        "\\s+>>>\\s+(start|start-ext)\\s+Nest=(\\d+)"
        + "\\s+([A-Za-z_0-9]*?)\\s+([A-Za-z\\._0-9]+)");
    pcBeginPattern = Pattern.compile(
        "\\s>>>>>\\s+Begin\\s+([A-Za-z\\._0-9]+)\\s+level"
        + "\\s+(\\d+)\\s+row\\s+(\\d+)");
    pcEndPattern = Pattern.compile("\\s+<<<\\s(end|end-ext)"
        + "\\s+Nest=(\\d+)\\s+([A-Za-z0-9_]*?)\\s+([A-Za-z\\._0-9]+)");
    pcRelDispProcStartPattern =
        Pattern.compile("Starting\\sRelated\\sDisplay\\sprocessing");
    pcRelDispProcEndPattern =
        Pattern.compile("Finished\\sRelated\\sDisplay\\sprocessing");
    pcFldDefaultPattern =
        Pattern.compile("([A-Z_0-9]+)\\.([A-Z_0-9]+)\\s+(constant\\s+)?default\\s+(from\\s+record\\s+)?(.+?)$");

    // Note: this pattern excludes any and all trailing semicolons.
    pcInstrPattern = Pattern.compile("\\s+\\d+:\\s+(.+?[;]*)$");
  }

  private TraceFileVerifier() {}

  /**
   * Initializes the verifier with the component runtime
   * profile that will be verified.
   * @param profile the component runtime profile to verify
   */
  public static void init(final ComponentRuntimeProfile profile) {
    /*
     * Open trace file for reading.
     */
    try {
      traceFileReader = new BufferedReader(new FileReader(
          new File("trace/" + profile.getTraceFileName())));
    } catch (final java.io.FileNotFoundException fnfe) {
      throw new OPSVMachRuntimeException(fnfe.getMessage(), fnfe);
    }

    ignoreStmtsInFile("ignoredSql_ORACLE.dat");
  }

  public static boolean isVerifierPaused() {
    return isPausedAndWaitingToSyncUp;
  }

  /**
   * Some SQL statements in the PS tracefile are not issued by OPS,
   * either b/c they're unnecessary/redundant or relate to functionality
   * not yet supported. Passing a file containing a list of SQL statements
   * to this method will cause the verifier to ignore them if they are found
   * in the tracefile.
   * @param filename name of file in classpath containing SQL stmts to ignore.
   */
  public static void ignoreStmtsInFile(final String filename) {
    try {
      final Resource ignoredSqlRsrc = new ClassPathResource(filename);
      final BufferedReader ignoreFileReader = new BufferedReader(
          new FileReader(ignoredSqlRsrc.getFile()));

      String line;
      while ((line = ignoreFileReader.readLine()) != null) {
        ignoredStmts.put(line, true);
      }
      ignoreFileReader.close();
    } catch (final java.io.FileNotFoundException fnfe) {
      throw new OPSVMachRuntimeException(fnfe.getMessage(), fnfe);
    } catch (final java.io.IOException ioe) {
      throw new OPSVMachRuntimeException(ioe.getMessage(), ioe);
    }
  }

  /**
   * The verifier will not fail if it is passed an emission that does not
   * correspond to the next emission in the PS tracefile via this method.
   * @param opsEmission emission from OPS that should not be enforced
   */
  public static void submitUnenforcedEmission(final IEmission opsEmission) {
//    log.debug(opsEmission);
    unenforcedEmissions.add(opsEmission);
  }

  /**
   * The verifier WILL fail if it is passed an emission that does not
   * correspond to the next emission in the PS tracefile via this method.
   * @param opsEmission emission from OPS that SHOULD be enforced
   */
  public static void submitEnforcedEmission(final IEmission opsEmission) {
    if(!(opsEmission instanceof OPSStmt)) {
      log.debug(opsEmission);
    }

    if (isPausedAndWaitingToSyncUp) {
      log.warn("[PAUSED] TRACE FILE VERIFIER IS PAUSED and waiting for interpreter "
        + "to emit: {}", emissionToSyncUpOn);
    }

    IEmission traceEmission;
    if (coverageAreaStartLineNbr == 0) {
      /*
       * If this is the first emission being matched, seek
       * to the match in the trace file. It is assumed that the first
       * OPS emission is a SQL statement due to the need to get component
       * metadata; if this routine is used to check tracefiles that have been
       * generated with a non-empty cache, this assumption may not be valid.
       */
      do {
        traceEmission = getNextTraceEmission();
      } while (traceEmission != null
            && !opsEmission.equals(traceEmission));

      if (traceEmission != null) {
        coverageAreaStartLineNbr = currTraceLineNbr;
        numEnforcedSQLEmissions++;
        return;
      }
    } else {
      if (isPausedAndWaitingToSyncUp) {
        traceEmission = emissionToSyncUpOn;
      } else {
        traceEmission = getNextTraceEmission();
      }
    }

    if (traceEmission == null) {
      throw new OPSVMachRuntimeException("Reached EOF of trace file; no "
          + "emission match is possible.");
    }

    if (opsEmission.equals(traceEmission)) {
      // Increment emission-specific counter.
      if (opsEmission instanceof OPSStmt) {
        numEnforcedSQLEmissions++;
      } else {
        numPCEmissionMatches++;
      }

      if (isPausedAndWaitingToSyncUp) {
        log.warn("[RESUMED] Trace file verifier is now synced up with interpreter on matching "
            + "emission: {}", opsEmission);
        isPausedAndWaitingToSyncUp = false;
        emissionToSyncUpOn = null;
      }
    } else if (isPausedAndWaitingToSyncUp) {
      log.warn("[SKIPPED] Trace file verifier is paused and waiting to sync up; disregarding "
          + "this OPS emission: {}", opsEmission);

    /*
     * Look at DERIVED_CS.SRVC_IND_NEG.RowInit. There is a Break statement without a trailing
     * semicolon. Although this is legal (in PeopleCode, semicolons are optional for the last
     * statement in any statement list), the PeopleTools bytecode interpreter does not appear
     * to interpret this correctly in the context of When statement lists in Evaluate statements;
     * it appears to skip When branches until it reaches the next statement with a semicolon.
     * I do not want to have to add semicolons to all programs where this is the case, so I am going
     * to temporarily pause trace file verification until the interpreter emits that next
     * statement with a semicolon. In theory, this should only cause the verifier to be paused for
     * a few emissions.
     */
    } else if (opsEmission instanceof PCInstruction
        && traceEmission instanceof PCInstruction
        && ((PCInstruction) traceEmission).getInstruction().equals("Break")) {

      // If we don't have a match and the trace file emitted a Break w/o semicolon, pause
      // and wait for interpreter to sync up.
      isPausedAndWaitingToSyncUp = true;
      emissionToSyncUpOn = getNextTraceEmission();

      log.warn("[PAUSING] Trace file verification; trace file expects Break w/o semicolon; "
          + "this is a known problem emission, so we will wait for interpreter to sync up "
          + "on the trace emission immediately after it: {}", emissionToSyncUpOn);
    } else if (opsEmission instanceof PCInstruction
        && ((PCInstruction) opsEmission).isOptional()) {

      // Unlike above, we do NOT skip to the next trace emission; OPS must match against
      // this one.
      isPausedAndWaitingToSyncUp = true;
      emissionToSyncUpOn = traceEmission;

      log.warn("[PAUSING] Trace file verification; OPS emitted optional instruction. Disregarding "
          + "and waiting for OPS to emit: {}", emissionToSyncUpOn);

      numOPSOptionalPCInstrsEmitted++;

    } else {
      log.fatal("=== Emission Mismatch! =======================");
      log.fatal("OPS emitted: {}", opsEmission);
      log.fatal("Trace file expects: {}", traceEmission);
      throw new OPSVMachRuntimeException("Emission mismatch.");
    }
  }

  private static IEmission getNextTraceEmission() {

    // do-while because we want to check the line returned from the prior call
    // to getNextTraceLine().
    do {
      final Matcher sqlMatcher = sqlTokenPattern.matcher(currTraceLine);
      if (sqlMatcher.find()) {
        numTraceSQLStmts++;

        final PSStmt psStmt = new PSStmt(sqlMatcher.group(1), currTraceLineNbr);

        // If the stmt is in the ignored file, skip it.
        if (ignoredStmts.containsKey(psStmt.getOriginalStmt())) {
          numTraceSQLStmtsIgnored++;
          continue;
        }

        // Keep reading lines for bind values.
        while ((currTraceLine = getNextTraceLine()) != null) {
          final Matcher bindValMatcher = bindValPattern.matcher(currTraceLine);
          if (bindValMatcher.find()) {
            psStmt.getBindVals().put(Integer.parseInt(bindValMatcher
                .group(GROUP1)), bindValMatcher.group(GROUP2));
          } else {
            // statement has one or more bind values.
            return psStmt;
          }
        }
        // statement has no bind values.
        return psStmt;
      }

      final Matcher pcStartMatcher = pcStartPattern.matcher(currTraceLine);
      if (pcStartMatcher.find()) {
        // We don't want the next call to check this line again.
        currTraceLine = getNextTraceLine();
        return new PCStart(pcStartMatcher.group(GROUP1),
            pcStartMatcher.group(GROUP2), pcStartMatcher.group(GROUP3),
            pcStartMatcher.group(GROUP4));
      }

      final Matcher pcBeginMatcher = pcBeginPattern.matcher(currTraceLine);
      if (pcBeginMatcher.find()) {
        // We don't want the next call to check this line again.
        currTraceLine = getNextTraceLine();
        return new PCBegin(pcBeginMatcher.group(GROUP1),
            Integer.parseInt(pcBeginMatcher.group(GROUP2)),
            Integer.parseInt(pcBeginMatcher.group(GROUP3)));
      }

      final Matcher pcInstrMatcher = pcInstrPattern.matcher(currTraceLine);
      if (pcInstrMatcher.find()) {
        // We don't want the next call to check this line again.
        currTraceLine = getNextTraceLine();
        return new PCInstruction(pcInstrMatcher.group(GROUP1));
      }

      final Matcher pcEndMatcher = pcEndPattern.matcher(currTraceLine);
      if (pcEndMatcher.find()) {
        // We don't want the next call to check this line again.
        currTraceLine = getNextTraceLine();
        return new PCEnd(pcEndMatcher.group(GROUP1),
            pcEndMatcher.group(GROUP2), pcEndMatcher.group(GROUP3),
            pcEndMatcher.group(GROUP4));
      }

      final Matcher pcFldDefaultMatcher = pcFldDefaultPattern.matcher(currTraceLine);
      if (pcFldDefaultMatcher.find()) {
        // We don't want the next call to check this line again.
        currTraceLine = getNextTraceLine();
        final PCFldDefaultEmission fldDefEmission =
            new PCFldDefaultEmission(pcFldDefaultMatcher.group(GROUP1),
            pcFldDefaultMatcher.group(GROUP2), pcFldDefaultMatcher.group(GROUP5));
        if (pcFldDefaultMatcher.group(GROUP3) != null) {
          fldDefEmission.setFromConstantFlag();
        }
        if (pcFldDefaultMatcher.group(GROUP4) != null) {
          fldDefEmission.setFromRecordFlag();
        }
        return fldDefEmission;
      }

      final Matcher pcRelDispProcStartMatcher =
          pcRelDispProcStartPattern.matcher(currTraceLine);
      if (pcRelDispProcStartMatcher.find()) {

        // Discard all tracefile emissions until end of rel disp proc is seen.
        while ((currTraceLine = getNextTraceLine()) != null) {
          final Matcher pcRelDispProcEndMatcher =
              pcRelDispProcEndPattern.matcher(currTraceLine);
          if (pcRelDispProcEndMatcher.find()) {
            break;
          }
        }
      }
    } while ((currTraceLine = getNextTraceLine()) != null);
    return null;
  }

  private static String getNextTraceLine() {
    String line = null;

    try {
      line = traceFileReader.readLine();
      currTraceLineNbr++;
    } catch (final java.io.IOException ioe) {
      throw new OPSVMachRuntimeException(ioe.getMessage(), ioe);
    }
    return line;
  }

  /**
   * External code should call this method to signal to the verifier
   * that no more emissions will be emitted.
   */
  public static void closeTraceFile() {
    try {
      traceFileReader.close();
    } catch (final java.io.IOException ioe) {
      log.warn("Encountered IOException while attempting to close trace file.");
    }
  }

  /**
   * Outputs the verifier's info collected during execution. This method
   * should be called after all processing is complete, or if the OPS
   * runtime encounters an exception before successful completion, it
   * should be called prior to exiting.
   * @param mismatchFlag true if execution completed successfully,
   *    false otherwise
   */
  public static void logVerificationSummary(final boolean mismatchFlag) {

    if (!mismatchFlag) {
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
    log.info("Optional PC Emissions:\t\t\t{}", numOPSOptionalPCInstrsEmitted);
    log.info("Total Emissions:\t\t\t\t{}",
        numEnforcedSQLEmissions + unenforcedEmissions.size()
        + numPCEmissionMatches + numOPSOptionalPCInstrsEmitted);
    log.info("Component Structure Valid?\t\t\t\t{}",
        ComponentStructureVerifier.hasBeenVerified ? "YES" : "!!NO!!");
    log.info("Coverage Area Bounded?\t\t\t\t{}",
        mismatchFlag ? "!!NO!!" : "YES");
    log.info("Coverage Area (Start / End Lines):\tL_{}\t\tL_{}",
        coverageAreaStartLineNbr, coverageAreaEndLineNbr);
    log.info("\nNext unmatched emission in trace file: {}",
        getNextTraceEmission());
  }
}
