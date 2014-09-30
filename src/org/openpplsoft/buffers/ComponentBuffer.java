/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.buffers;

import java.util.List;
import java.util.Map;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.*;
import org.openpplsoft.pt.pages.*;
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

  public static void fireEvent(final PCEvent event) {
    cBuffer.fireEvent(event);
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
   * TODO(mquinn): complete this method.
   * Filling the component buffer involves multiple passes, total
   * involved is dynamic I believe. I'm calling this just the first
   * pass for now. The goal is to delegate as much of the filling part
   * to the buffers themselves, as that will be the same across all passes.
   */
  public static void firstPassFill() {
    IStreamableBuffer buf;

    ComponentBuffer.resetCursors();
    while ((buf = ComponentBuffer.next()) != null) {

      if (buf instanceof RecordBuffer) {
        final RecordBuffer rbuf = (RecordBuffer) buf;

        /*
         * For the first pass, only fill table and view
         * (i.e., not derived) buffers; individual calls may
         * be aborted if record conditions are not met (see
         * method implementation for details).
         */
        final Record recDefn = DefnCache.getRecord(rbuf.getRecName());
        if (recDefn.isTable() || recDefn.isView()) {
          rbuf.firstPassFill();
        }
      } else if(buf instanceof ScrollBuffer &&
          ((ScrollBuffer) buf).getScrollLevel() != 0) {
        // we only want scroll level 0; break beyond that
        break;
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
}
