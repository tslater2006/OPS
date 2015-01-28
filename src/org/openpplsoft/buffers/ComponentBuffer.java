/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.buffers;

import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.*;
import org.openpplsoft.pt.pages.*;
import org.openpplsoft.pt.peoplecode.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.sql.*;
import org.openpplsoft.trace.*;
import org.openpplsoft.types.*;

/**
 * Represents a PeopleTools component buffer.
 */
public final class ComponentBuffer {

  private static Logger log =
      LogManager.getLogger(ComponentBuffer.class.getName());

  private static Component compDefn;
  private static Record searchRecDefn;
  private static Set<String> prmEntries;

  private static int currScrollLevel;
  private static ScrollBuffer currSB;
  private static ScrollBuffer lvlZeroScrollBuffer;

  private static PTBufferRowset cBuffer;

  private ComponentBuffer() {}

  static {
    lvlZeroScrollBuffer = new ScrollBuffer(0, null, null);
    currSB = lvlZeroScrollBuffer;
  }

  /**
   * Initializes the component buffer with the provided Component defn.
   */
  public static void init(final Component c) {
    compDefn = c;

    searchRecDefn = DefnCache.getRecord(compDefn.getSearchRecordName());
    cBuffer = new PTRowsetTypeConstraint().allocBufferRowset(
        new SearchRecordBuffer(searchRecDefn));

    compDefn.getListOfComponentPC();
  }

  public static void fireEvent(final PCEvent event,
      final FireEventSummary fireEventSummary) {

    /*
     * If a Component PeopleCode program has been written for this component,
     * i.e., PreBuild or PostBuild, run it now.
     */
    final PeopleCodeProg compProg = compDefn.getProgramForEvent(event);
    if (compProg != null) {
      // Note: root component events like PreBuild and PostBuild always
      // run at scroll level 0 and row 0.
      final ExecContext eCtx = new ProgramExecContext(compProg, 0, 0);

      // IMPORTANT: Buffer context for Component-level events like PreBuild
      // and PostBuild have the first (and only) row of the level zero rowset
      // as their context.
      final InterpretSupervisor interpreter =
          new InterpretSupervisor(eCtx, getLevelZeroRowset().getRow(1));
      interpreter.run();
      fireEventSummary.incrementNumEventProgsExecuted();
    } else {
      cBuffer.fireEvent(event,fireEventSummary);
    }
  }

  /**
   * Component-level default processing invokes field default processing
   * on all fields (only those with a RecordField buffer will actually execute
   * processing).
   */
  public static void runDefaultProcessing() {

    FieldDefaultProcSummary fldDefProcSummary = null;

    // First, run field level default processing on all fields
    // (call on level 0 rowset b/c search record does not get processed).
    do {
      fldDefProcSummary = new FieldDefaultProcSummary();
      getLevelZeroRowset().runFieldDefaultProcessing(fldDefProcSummary);
    } while (fldDefProcSummary.doContinueDefProc());

    // Next, run FieldFormula programs (should be few, if any, since Oracle
    // has advised that no one use this event except to define external
    // function libraries).
    final FireEventSummary fieldFormulaSummary = new FireEventSummary();
    fireEvent(PCEvent.FIELD_FORMULA, fieldFormulaSummary);

    // Next, if any FieldFormula programs were just run, run
    // another round of default processing.
    if (fieldFormulaSummary.getNumEventProgsExecuted() > 0) {
      do {
        fldDefProcSummary = new FieldDefaultProcSummary();
        getLevelZeroRowset().runFieldDefaultProcessing(fldDefProcSummary);
      } while (fldDefProcSummary.doContinueDefProc());
    }
  }

  public static void materialize() {
    cBuffer.dynamicallyRegisterChildScrollBuffer(lvlZeroScrollBuffer);
  }

  public static PTBufferRecord getSearchRecord() {
    return cBuffer.getRow(1).getRecord(compDefn.getSearchRecordName());
  }

  public static Component getComponentDefn() {
    return compDefn;
  }

  /**
   * Get the scroll buffer that the ComponentBuffer is
   * currently pointing at.
   * @return the currently pointed at ScrollBuffer
   */
  public static ScrollBuffer getCurrentScrollBuffer() {
    return currSB;
  }

  public static PTBufferRowset getCBufferRowset() {
    return cBuffer;
  }

  public static PTBufferRowset getLevelZeroRowset() {
    // Remember, null is used here b/c the level 0 scroll does
    // not have a primary record.
    return cBuffer.getRow(1).getRowset(null);
  }

  /**
   * If the search record contains at least one key, fill the
   * search record with data.
   */
  public static void fillSearchRecord() {

    if (!searchRecDefn.hasAnySearchKeys()) {
      return;
    }

    OPSStmt ostmt = StmtLibrary.getSearchRecordFillQuery();
    OPSResultSet rs = ostmt.executeQuery();

    final int numCols = rs.getColumnCount();

    // search record may legitimately be empty, check before continuing.
    if (rs.next()) {
      rs.readIntoRecord(ComponentBuffer.getSearchRecord());
      if (rs.next()) {
        throw new OPSVMachRuntimeException(
            "Result set for search record fill has more than "
            + "one record.");
      }
      TraceFileVerifier.submitEnforcedEmission(
          new BeginScrolls(ScrollEmissionContext.SEARCH_RESULTS));
      cBuffer.emitScrolls(ScrollEmissionContext.SEARCH_RESULTS, 0);
      TraceFileVerifier.submitEnforcedEmission(new EndScrolls());
    }
  }

  /**
   * Reads the stream of page fields for each page in this
   * component definition, then builds appropriate buffers
   * to create the complete component buffer.
   */
  public static void assembleBuffers() {

    for (Page p : compDefn.getPages()) {
      p.recursivelyLoadSubpages();
    }

    for (Page p : compDefn.getPages()) {
      p.recursivelyLoadSecpages();
    }

    PgToken tok;
    PgTokenStream pfs;

    for (Page p : compDefn.getPages()) {
      pfs = new PgTokenStream(p.getPNLNAME());

      final Stack<ScrollMarker> scrollMarkers = new Stack<ScrollMarker>();
      scrollMarkers.push(new ScrollMarker(0, null, PFlag.PAGE));

      while ((tok = pfs.next()) != null) {

        //log.debug(tok);

        if (tok.hasFlag(PFlag.PAGE)) {
          final ScrollMarker sm = new ScrollMarker();
          sm.src = PFlag.PAGE;
          sm.primaryRecName = scrollMarkers.peek().primaryRecName;
          sm.scrollLevel = scrollMarkers.peek().scrollLevel;
          scrollMarkers.push(sm);
          continue;
        }

        if (tok.hasFlag(PFlag.END_OF_PAGE)) {
          while (scrollMarkers.peek().src == PFlag.SCROLL_START) {
            // pop interim scroll levels.
            scrollMarkers.pop();
          }
          // pop the matching page.
          scrollMarkers.pop();
          continue;
        }

        if (tok.hasFlag(PFlag.SCROLL_START)) {

          // This scroll may appear right after an unended scroll;
          // if so, pop the previous one.
          final ScrollMarker topSm = scrollMarkers.peek();
          if (topSm.src == PFlag.SCROLL_START
              && !tok.getPrimaryRecName().equals(topSm.primaryRecName)) {
            scrollMarkers.pop();
          }

          final ScrollMarker sm = new ScrollMarker();
          sm.src = PFlag.SCROLL_START;
          sm.primaryRecName = tok.getPrimaryRecName();
          sm.scrollLevel = scrollMarkers.peek().scrollLevel + tok.getOccursLevel();
          scrollMarkers.push(sm);
          continue;
        }

        // Remember: don't "continue" here, since SCROLL_LVL_DECREMENT
        // can be attached to regular fields.
        if (tok.hasFlag(PFlag.SCROLL_LVL_DECREMENT)) {
          scrollMarkers.pop();
        }

        if (tok.doesBelongInComponentBuffer()) {
          ComponentBuffer.addPageField(tok, scrollMarkers.peek().scrollLevel,
            scrollMarkers.peek().primaryRecName);
        }
      }

      if (scrollMarkers.size() != 0) {
        throw new OPSVMachRuntimeException("Scroll marker stack size "
            + "exceeds 0 at the end of the page token stream.");
      }
    }
  }

  public static void logPageHierarchyVisual() {

    PgToken tok;
    PgTokenStream pfs;

    for (Page p : compDefn.getPages()) {

      log.info("Root page: {}", p.getPNLNAME());

      String indent = "   ";
      pfs = new PgTokenStream(p.getPNLNAME());

      while ((tok = pfs.next()) != null) {

        if (tok.hasFlag(PFlag.PAGE)) {
          log.info("{}Page:{}", indent, tok);
          indent = indent.concat("   ");
          continue;
        }

        if (tok.hasFlag(PFlag.END_OF_PAGE)) {
          indent = indent.substring(3);
          log.info("{}{}", indent, tok);
          continue;
        }

        log.info("{}{}", indent, tok);
      }
    }
  }

  /**
   * Add a page field to the appropriate ScrollBuffer
   * in the ComponentBuffer.
   * @param tok the page field to add
   * @param level the scroll level on which the field occurs
   * @param primaryRecName the primary record name of the scroll
   *    on which the page field occurs
   */
  public static void addPageField(final PgToken tok,
      final int level, final String primaryRecName) {
    // Ensure that we're pointing at the correct scroll buffer.
    pointAtScroll(level, primaryRecName);
    currSB.addPageField(tok);
  }

  /**
   * Change the ScrollBuffer that the ComponentBuffer currently points at.
   * @param targetScrollLevel the scroll level of the scroll buffer to
   *    point to
   * @param targetPrimaryRecName the primary record name for the scroll
   *    buffer to point to
   */
  public static void pointAtScroll(final int targetScrollLevel,
      final String targetPrimaryRecName) {

    // Remember that there's only one scroll level at 0.
    if (currSB.getScrollLevel() == targetScrollLevel
        && (currSB.getScrollLevel() == 0
          || currSB.getPrimaryRecName().equals(targetPrimaryRecName))) {
      return;
    }

    while (currScrollLevel < targetScrollLevel) {
      currSB = currSB.getChildScroll(targetPrimaryRecName);
      currScrollLevel = currSB.getScrollLevel();
    }

    while (currScrollLevel > targetScrollLevel) {
      currSB = currSB.getParentScrollBuffer();
      currScrollLevel = currSB.getScrollLevel();
    }

    // The scroll level may not have changed, but if the
    // targeted primary rec name differs from the current,
    // we need to change buffers.
    if (currScrollLevel > 0
        && !currSB.getPrimaryRecName().equals(targetPrimaryRecName)) {
      currSB = currSB.getParentScrollBuffer()
          .getChildScroll(targetPrimaryRecName);
      currScrollLevel = currSB.getScrollLevel();
    }
  }

  /**
   * Retrieve the next buffer out of the ComponentBuffer;
   * buffers are read out of the component buffer in a recursive,
   * depth-first manner.
   * @return the next buffer in the read sequence
   */
  public static IStreamableBuffer next() {
    return lvlZeroScrollBuffer.next();
  }

  /**
   * Reset all buffer cursors; this should be done before using
   * {@code next()} to read all buffers out of the ComponentBuffer.
   * This reset call will propagate recursively to all child buffers.
   */
  public static void resetCursors() {
    lvlZeroScrollBuffer.resetCursors();
  }

  public static void expandRecordBuffersWhereNecessary() {
    IStreamableBuffer buf;
    ComponentBuffer.resetCursors();
    while ((buf = ComponentBuffer.next()) != null) {
      if (buf instanceof RecordFieldBuffer) {
        ((RecordFieldBuffer) buf).expandParentRecordBufferIfNecessary();
      }
    }
  }

  public static void addEffDtKeyWhereNecessary() {
    IStreamableBuffer buf;
    ComponentBuffer.resetCursors();
    while ((buf = ComponentBuffer.next()) != null) {
      if (buf instanceof RecordBuffer) {
        ((RecordBuffer) buf).addEffDtKeyIfNecessary();
      }
    }
  }

  /**
   * Prints the structure of this ComponentBuffer; includes scroll levels,
   * primary record names, and all included child records. Indentation is
   * printed where appropriate.
   */
  public static void printStructure() {
    int indent = 0;
    final int INDENT_INCREMENT = 3;
    IStreamableBuffer buf;
    ComponentBuffer.resetCursors();
    while ((buf = ComponentBuffer.next()) != null) {
      if (buf instanceof ScrollBuffer) {
        final ScrollBuffer sbuf = (ScrollBuffer) buf;
        indent = sbuf.getScrollLevel() * INDENT_INCREMENT;
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < indent; i++) {
          b.append(" ");
        }
        b.append("Scroll - Level ").append(sbuf.getScrollLevel())
            .append("\tPrimary Record: ").append(sbuf.getPrimaryRecName());
        for (int i = 0; i < indent; i++) {
          b.append(" ");
        }
        log.debug(b.toString());
        log.debug("=======================================================");
      } else if (buf instanceof RecordBuffer) {
        final RecordBuffer rbuf = (RecordBuffer) buf;
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < indent; i++) {
          b.append(" ");
        }
        b.append(" + ").append(rbuf.getRecDefn().RECNAME);
        log.debug(b.toString());
      } else {

        final RecordFieldBuffer fbuf = (RecordFieldBuffer) buf;
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < indent; i++) {
          b.append(" ");
        }
        b.append("   - ").append(fbuf.getFldName());
        if (fbuf.getPageFieldToks().size() > 0) {
          b.append(" | ").append(fbuf.getPageFieldToks());
        } else {
          b.append(" | <no page field toks>");
        }
        log.debug(b.toString());
      }
    }
  }

  /**
   * Fill this record buffer as part of the
   * first pass fill routine. In order to be filled, a record
   * must 1) be a table or view (should be checked by caller)
   * and 2) have either at least one
   * required field OR have no keys. The fill may be aborted
   * during the StmtLibrary call if a required key does not have
   * a matching value in the scroll hierarchy.
   */
  public static void firstPassFill() {

    // Remember: null is used to get the rowset b/c the rowset
    // at scroll level zero has no (null) primary record name.
    final PTBufferRow levelZeroRow = cBuffer.getRow(1).getRowset(null).getRow(1);

    for (Map.Entry<String, PTBufferRecord> entry
        : levelZeroRow.getRecordMap().entrySet()) {
      final PTBufferRecord record = entry.getValue();
      final Record recDefn = record.getRecDefn();
     /*
      * For the first pass, only fill table and view
      * (i.e., not derived) buffers; individual calls may
      * be aborted if record conditions are not met (see
      * method implementation for details).
      */
      if (record.getRecBuffer().doesContainStructuralFields()
          && (recDefn.isTable() || recDefn.isView())) {
        record.firstPassFill();
      }
    }
  }

  public static void emitPRM() {

    final List<ComponentPeopleCodeProg> compProgs = compDefn.getListOfComponentPC();
    final Set<String> activePfsRefs = new HashSet<String>();
    final Set<String> compPfsRefs = new HashSet<String>();
    final Set<String> recFldRefs = new TreeSet<String>();
    final String RECFIELD_TO_FIND = "aDERIVED_CC.FERPA_ACTVTS_PB";

    final String activePageName =
                  ((PTString) Environment.getSystemVar("%Page")).read();
    for (final Page p : compDefn.getPages()) {
      boolean isActivePage = (activePageName.equals(p.getPNLNAME()));
      final PgTokenStream pfs = new PgTokenStream(p.getPNLNAME());
      PgToken tok;
      PgToken currPageTok = null;
      while ((tok = pfs.next()) != null) {

        if (tok.hasFlag(PFlag.PAGE)) {
          currPageTok = tok;
        }

        final String entry = tok.getRecName() + "." + tok.getFldName();
        if (!tok.isRelatedDisplay()) {
          compPfsRefs.add(entry);
          log.debug("Adding to compPfsRefs: {}", entry);
          if (isActivePage) {
            activePfsRefs.add(entry);
            log.debug("Adding to activePfsRefs: {}", entry);
          }
        } else {
          continue;
        }

        /*if (tok.hasFlag(PFlag.PUSHBTN_LINK)) {
          if (tok.hasSubPnlName()) {
            recFldRefs.add(entry);
            if (RECFIELD_TO_FIND.startsWith(tok.getRecName())
                && RECFIELD_TO_FIND.endsWith(tok.getFldName())) {
              throw new OPSVMachRuntimeException("HERE0, recfield on page: "
                  + currPageTok + "; tok is: " + tok);
            }
          }
        }*/
      }
    }

    /*
     * Collect PRM entries from Page Activate PeopleCode.
     */
    final Page activePage = DefnCache.getPage(activePageName);
    final PeopleCodeProg pageActivateProg = activePage.getPageActivateProg();
    final Set<String> progActivateSet =
        pageActivateProg.getPRMRecFields();
    if (progActivateSet.contains(RECFIELD_TO_FIND)) {
      throw new OPSVMachRuntimeException("[ACTIVATE] pageActivateProg is "
          + pageActivateProg);
    }
    recFldRefs.addAll(progActivateSet);


    /*
     * Collect PRM entries from Component PeopleCode programs.
     */
    for (final ComponentPeopleCodeProg prog1 : compProgs) {

      //prog1.listRefsToRecordFieldAndRecur(1, RECFIELD_TO_FIND,
      //  new HashSet<String>());
      log.debug("[PRM]->{}", prog1);

      final Set<String> prog1Set = prog1.getPRMRecFields();
      if (prog1Set.contains(RECFIELD_TO_FIND)) {
        throw new OPSVMachRuntimeException("[COMP1] prog1 is " + prog1);
      }
      recFldRefs.addAll(prog1Set);
      if (1 == 1) continue;

      if (prog1.getEvent().equals("PreBuild")
          || prog1.getEvent().equals("PostBuild")) {
        prog1.loadDefnsAndPrograms();
        for (final PeopleCodeProg prog2 : prog1.getReferencedProgs()) {
     //if (!prog2.getEvent().equals("FieldFormula")) {
          final Set<String> prog2Set =
              prog2.getPRMRecFields();
          log.debug("[PRM]   ->{} : {}", prog2, prog2Set);

          /* BEGIN TEMP */
          final java.util.Iterator<String> iter = prog2Set.iterator();
          while(iter.hasNext()) {
            final String ref = iter.next();
            if (ref.startsWith("CLASS_SRCH_WRK2")) {
              iter.remove();
            }
          }
          /* END TEMP */
          // Only add rec flds that are in the page stream.
          prog2Set.retainAll(compPfsRefs);

          if (prog2Set.contains(RECFIELD_TO_FIND)) {
            throw new OPSVMachRuntimeException("[COMP2] prog2 is " + prog2
                + "; prog1 is " + prog1);
          }
          recFldRefs.addAll(prog2Set);
     //}
          /*prog2.loadDefnsAndPrograms();
          for (final PeopleCodeProg prog3 : prog2.getReferencedProgs()) {
            final Set<String> prog3Set =
                prog3.getUniqueRecFieldRefsForPRMInclusion();
            //log.debug("[PRM]      ->{}", prog3);

            // Only add rec flds that are in the page stream.
            prog3Set.retainAll(compPfsRefs);

            if (prog3Set.contains(RECFIELD_TO_FIND)) {
                throw new OPSVMachRuntimeException("[COMP3] prog3 is " + prog3
                  + "; prog2 is " + prog2 + "; prog1 is " + prog1);
            }
            recFldRefs.addAll(prog3Set);
          }*/
        }
      }
    }

    /*
     * Collect PRM entries from Record PeopleCode programs.
     */
    IStreamableBuffer buf;
    ComponentBuffer.resetCursors();
    while ((buf = ComponentBuffer.next()) != null) {
      if (buf instanceof RecordFieldBuffer) {
        final RecordFieldBuffer fbuf = (RecordFieldBuffer) buf;
        final Record recDefn = fbuf.getRecDefn();
        // Related display records will not have PeopleCode run on them,
        // and thus should not have their reference included in the PRM listing.
        if (fbuf.getParentRecordBuffer().isRelatedDisplayRecBuffer()) {
          continue;
        }

        for (final RecordPeopleCodeProg prog
            : recDefn.getRecordProgsForField(fbuf.getFldName())) {

          //prog.listRefsToRecordFieldAndRecur(1, RECFIELD_TO_FIND,
            //new HashSet<String>());

            //: recDefn.getAllRecordProgs()) {
          final Set<String> progSet = prog.getPRMRecFields();
          if (progSet.contains(RECFIELD_TO_FIND)) {
            throw new OPSVMachRuntimeException("HERE3; record is " + recDefn
                + ", prog is " + prog);
          }
          recFldRefs.addAll(progSet);
          if (1 == 1) continue;

          if (progSet.size() > 0) {
            for (final RecordPeopleCodeProg prog2: recDefn.getAllRecordProgs()) {
              final Set<String> allProgSet = prog2.getPRMRecFields();

              // Only add rec flds that are in the page stream.
              allProgSet.retainAll(activePfsRefs);

              if (allProgSet.contains(RECFIELD_TO_FIND)) {
                throw new OPSVMachRuntimeException("HERE4; record is " + recDefn
                    + ", prog2 is " + prog2 + "; prog is " + prog);
              }

              recFldRefs.addAll(allProgSet);
            }
          }
        }
      }
    }

    TraceFileVerifier.submitEnforcedEmission(
        new PRMHeader(compDefn.getComponentName(), "ENG", compDefn.getMarket(),
            recFldRefs.size()));

    log.debug("PRM entries (count={}):", recFldRefs.size());
    recFldRefs.stream().forEach(r -> log.debug("  {}", r));

    recFldRefs.stream().forEach(r ->
        TraceFileVerifier.submitEnforcedEmission(new PRMEntry(r)));
  }

  private static class ScrollMarker {
    private String primaryRecName;
    private int scrollLevel;
    private PFlag src;

    public ScrollMarker() {}

    public ScrollMarker(final int s, final String p, final PFlag a) {
      this.scrollLevel = s;
      this.primaryRecName = p;
      this.src = a;
    }
  }
}
