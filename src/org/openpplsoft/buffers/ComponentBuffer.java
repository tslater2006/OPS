/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.buffers;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.*;
import org.openpplsoft.pt.pages.*;
import org.openpplsoft.pt.peoplecode.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.sql.*;
import org.openpplsoft.types.*;

/**
 * Represents a PeopleTools component buffer.
 */
public final class ComponentBuffer {

  private static Logger log =
      LogManager.getLogger(ComponentBuffer.class.getName());

  private static Component compDefn;
  private static Record searchRecDefn;

  private static int currScrollLevel;
  private static ScrollBuffer currSB;
  private static ScrollBuffer lvlZeroScrollBuffer;

  private static PTRowset cBuffer;

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

    // Allocate a new row (with null parent) for use as the component buffer.
    searchRecDefn = DefnCache.getRecord(compDefn.getSearchRecordName());
    cBuffer = new PTRowsetTypeConstraint().alloc(null, searchRecDefn);

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
      final ExecContext eCtx = new ProgramExecContext(compProg);

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

    // First, run field level default processing on all fields
    // (call on level 0 rowset b/c search record does not get processed).
    final FieldDefaultProcSummary fldDefProcSummary = new FieldDefaultProcSummary();
    getLevelZeroRowset().runFieldDefaultProcessing(fldDefProcSummary);

    throw new OPSVMachRuntimeException("TODO: Run FieldFormula.");

  }

  public static void materialize() {
    cBuffer.registerChildScrollDefn(lvlZeroScrollBuffer);
  }

  public static PTRecord getSearchRecord() {
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

  public static PTRowset getCBufferRowset() {
    return cBuffer;
  }

  public static PTRowset getLevelZeroRowset() {
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
    ResultSet rs = null;

    try {
      rs = ostmt.executeQuery();

      final ResultSetMetaData rsMetadata = rs.getMetaData();
      final int numCols = rsMetadata.getColumnCount();

      // search record may legitimately be empty, check before continuing.
      if (rs.next()) {
        final PTRecord searchRecord = ComponentBuffer.getSearchRecord();
        for (int i = 1; i <= numCols; i++) {
          final String colName = rsMetadata.getColumnName(i);
          final String colTypeName = rsMetadata.getColumnTypeName(i);
          final PTField fldObj = searchRecord.getFieldRef(colName).deref();
          GlobalFnLibrary.readFieldFromResultSet(fldObj,
              colName, colTypeName, rs);
        }
        if (rs.next()) {
          throw new OPSVMachRuntimeException(
              "Result set for search record fill has more than "
              + "one record.");
        }
      }
    } catch (final java.sql.SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    } finally {
      try {
        if (rs != null) { rs.close(); }
        if (ostmt != null) { ostmt.close(); }
      } catch (final java.sql.SQLException sqle) {
        log.warn("Unable to close rs and/or ostmt in finally block.");
      }
    }
  }

  /**
   * Reads the stream of page fields for each page in this
   * component definition, then builds appropriate buffers
   * to create the complete component buffer.
   */
  public static void assembleStructure() {

    for (Page p : compDefn.getPages()) {
      p.recursivelyLoadSubpages();
    }

    for (Page p : compDefn.getPages()) {
      p.recursivelyLoadSecpages();
    }

    PgToken tok;
    PgTokenStream pfs;

    final byte REL_DISP_FLAG = (byte) 16;

    for (Page p : compDefn.getPages()) {
      pfs = new PgTokenStream(p.getPNLNAME());

      final Stack<ScrollMarker> scrollMarkers = new Stack<ScrollMarker>();
      scrollMarkers.push(new ScrollMarker(0, null, PFlag.PAGE));

      while ((tok = pfs.next()) != null) {

        //log.debug(tok);

        if (tok.flags.contains(PFlag.PAGE)) {
          final ScrollMarker sm = new ScrollMarker();
          sm.src = PFlag.PAGE;
          sm.primaryRecName = scrollMarkers.peek().primaryRecName;
          sm.scrollLevel = scrollMarkers.peek().scrollLevel;
          scrollMarkers.push(sm);
          continue;
        }

        if (tok.flags.contains(PFlag.END_OF_PAGE)) {
          while (scrollMarkers.peek().src == PFlag.SCROLL_START) {
            // pop interim scroll levels.
            scrollMarkers.pop();
          }
          // pop the matching page.
          scrollMarkers.pop();
          continue;
        }

        if (tok.flags.contains(PFlag.SCROLL_START)) {

          // This scroll may appear right after an unended scroll;
          // if so, pop the previous one.
          final ScrollMarker topSm = scrollMarkers.peek();
          if (topSm.src == PFlag.SCROLL_START
              && !tok.primaryRecName.equals(topSm.primaryRecName)) {
            scrollMarkers.pop();
          }

          final ScrollMarker sm = new ScrollMarker();
          sm.src = PFlag.SCROLL_START;
          sm.primaryRecName = tok.primaryRecName;
          sm.scrollLevel = scrollMarkers.peek().scrollLevel + tok.OCCURSLEVEL;
          scrollMarkers.push(sm);
          continue;
        }

        // Remember: don't "continue" here, since SCROLL_LVL_DECREMENT
        // can be attached to regular fields.
        if (tok.flags.contains(PFlag.SCROLL_LVL_DECREMENT)) {
          scrollMarkers.pop();
        }

        if (tok.doesBelongInComponentStructure()) {
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
        log.info(b.toString());
        log.info("=======================================================");
      } else if (buf instanceof RecordBuffer) {
        final RecordBuffer rbuf = (RecordBuffer) buf;
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < indent; i++) {
          b.append(" ");
        }
        b.append(" + ").append(rbuf.getRecName());
        log.info(b.toString());
      } else {

        final RecordFieldBuffer fbuf = (RecordFieldBuffer) buf;
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < indent; i++) {
          b.append(" ");
        }
        b.append("   - ").append(fbuf.getFldName());
        log.info(b.toString());
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
    final PTRow levelZeroRow = cBuffer.getRow(1).getRowset(null).getRow(1);

    for (Map.Entry<String, PTRecord> entry
        : levelZeroRow.getRecordMap().entrySet()) {
      final PTRecord record = entry.getValue();
      final Record recDefn = record.getRecDefn();
     /*
      * For the first pass, only fill table and view
      * (i.e., not derived) buffers; individual calls may
      * be aborted if record conditions are not met (see
      * method implementation for details).
      */
      if (recDefn.isTable() || recDefn.isView()) {
        record.firstPassFill();
      }
    }
  }

/*  public static void printContents() {
    log.debug("======= COMPONENT BUFFER =========");
    log.debug("== BEGIN SEARCH RECORD ===========");
    for (Map.Entry<String, PTImmutableReference<PTField>> entry
        : searchRecord.getFieldRefs().entrySet()) {
      final PTPrimitiveType fldValue = entry.getValue().deref().getValue();
      log.debug("{}={}", entry.getKey(), fldValue.readAsCompBufferOutput());
    }
    log.debug("== END SEARCH RECORD =============");

    final PTRowset lvlZeroRowset = lvlZeroScrollBuffer.ptGetRowset();
    for (int i = 1; i <= lvlZeroRowset.getActiveRowCount(); i++) {
      log.debug("Level 0, row {}", i - 1);
      for (RecordBuffer recBuf : lvlZeroScrollBuffer.getOrderedRecBuffers()) {
        final List<RecordFieldBuffer> recFldBuffers = recBuf.getFieldBuffers();
        for (RecordFieldBuffer recFldBuf : recFldBuffers) {
          log.debug("{}={}", recFldBuf.getFldName(), lvlZeroRowset.getRow(i).getRecord(
              recBuf.getRecName()).getFieldRef(recFldBuf.getFldName()).deref().getValue().readAsCompBufferOutput());
        }
      }
    }
  }*/

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
