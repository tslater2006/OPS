/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.buffers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.*;
import org.openpplsoft.pt.pages.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.types.*;

/**
 * Represents a PeopleTools component buffer.
 */
public final class ComponentBuffer {

  private static Logger log =
      LogManager.getLogger(ComponentBuffer.class.getName());

  private static int currScrollLevel;
  private static ScrollBuffer currSB;
  private static ScrollBuffer compBuffer;
  private static PTRecord searchRecord;

  private ComponentBuffer() {}

  static {
    compBuffer = new ScrollBuffer(0, null, null);
    currSB = compBuffer;
  }

  /**
   * Return the search record underlying this component.
   * @return the underlying search record
   */
  public static PTRecord getSearchRecord() {
    return searchRecord;
  }

  /**
   * Set the search record to be used by this component.
   * @param r the search record to use
   */
  public static void setSearchRecord(final PTRecord r) {
    searchRecord = r;
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
    return compBuffer.next();
  }

  /**
   * Reset all buffer cursors; this should be done before using
   * {@code next()} to read all buffers out of the ComponentBuffer.
   * This reset call will propagate recursively to all child buffers.
   */
  public static void resetCursors() {
    compBuffer.resetCursors();
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
      }
    }
  }
}
