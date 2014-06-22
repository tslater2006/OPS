/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.buffers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.sql.*;
import org.openpplsoft.types.*;

/**
 * Represents a PeopleTools record buffer.
 */
public class RecordBuffer implements IStreamableBuffer {

  private static Logger log =
      LogManager.getLogger(RecordBuffer.class.getName());

  private ScrollBuffer sbuf;
  private String recName;
  private int scrollLevel;
  private boolean isPrimaryScrollRecordBuffer, hasBeenExpanded;
  private Map<String, RecordFieldBuffer> fieldBufferTable;
  private List<RecordFieldBuffer> fieldBuffers;

  /*
   * IMPORTANT: I am using this to hold the actual data in the
   * buffer rather than store it in the individual field buffers.
   * At least for now, the RecordFieldBuffers are the authoritative source
   * on *which* fields are available in the component. The PTRecord
   * contains the data.
   */
  private PTRecord underlyingRecord;

  // Used for reading.
  private boolean hasEmittedSelf;
  private int fieldBufferCursor;

  /**
   * Creates a new record buffer.
   * @param p the ScrollBuffer to which the record belongs.
   * @param r the name of the record (RECNAME)
   * @param s the scroll level for this particular buffer
   * @param primaryRecName the primary record name of the
   *    scroll to which this record belongs.
   */
  public RecordBuffer(final ScrollBuffer p, final String r,
      final int s, final String primaryRecName) {
    this.sbuf = p;
    this.recName = r;
    this.scrollLevel = s;

    if (primaryRecName != null && this.recName.equals(primaryRecName)) {
      this.isPrimaryScrollRecordBuffer = true;
    }

    this.fieldBufferTable = new HashMap<String, RecordFieldBuffer>();
    this.fieldBuffers = new ArrayList<RecordFieldBuffer>();

    /*
     * TODO(mquinn): This may not be the correct approach.
     * Not sure if EFFDT should always be in the component buffer, or if
     * it should only be there if based on the presence of specific keys.
     * This may even need to be broader than EFFDT; i.e., if any level-0
     * record contains keys, add them now.
     */
    final Record recDefn = DefnCache.getRecord(this.recName);
    final RecordField EFFDT = recDefn.fieldTable.get("EFFDT");
    if (EFFDT != null && EFFDT.isKey()) {
      this.addPageField(this.recName, "EFFDT");
    }

    this.underlyingRecord = PTRecord.getSentinel().alloc(recDefn);
  }

  /**
   * Get the name of the record underlying this buffer.
   * @return the RECNAME of the underlying record
   */
  public String getRecName() {
    return this.recName;
  }

  /**
   * Get the scroll level at which this record buffer exists.
   * @return the scroll level at which this record buffer exists
   */
  public int getScrollLevel() {
    return this.scrollLevel;
  }

  /**
   * Get whether or not this record buffer is the primary
   * record for its parent ScrollBuffer.
   * @return true if this record is the scroll's primary record,
   *    false otherwise
   */
  public boolean getIsPrimaryScrollRecordBuffer() {
    return this.isPrimaryScrollRecordBuffer;
  }

  /**
   * Get this record buffer's parent scroll buffer.
   * @return the parent ScrollBuffer for this RecordBuffer
   */
  public ScrollBuffer getParentScrollBuffer() {
    return this.sbuf;
  }

  /**
   * UNDER DEVELOPMENT: Fill this record buffer as part of the
   * first pass fill routine (under heavy development).
   */
  public void firstPassFill() {
    OPSStmt ostmt = StmtLibrary.prepareFirstPassFillQuery(this);
    ResultSet rs = null;

    try {

      /*
       * If null comes back, one or more key values is not
       * available, and thus the fill cannot be run.
       */
      if (ostmt == null) { return; }

      rs = ostmt.executeQuery();

      final ResultSetMetaData rsMetadata = rs.getMetaData();
      final int numCols = rsMetadata.getColumnCount();

      // NOTE: record may legitimately be empty.
      if (rs.next()) {
        final Record recDefn = DefnCache.getRecord(this.recName);
        for (int i = 1; i <= numCols; i++) {
          final String colName = rsMetadata.getColumnName(i);
          final String colTypeName = rsMetadata.getColumnTypeName(i);
          final PTField fldObj = this.underlyingRecord.getField(colName);
          log.debug("Before: {} = {}", colName, fldObj);
          GlobalFnLibrary.readFieldFromResultSet(fldObj,
              colName, colTypeName, rs);
          log.debug("After: {} = {}", colName, fldObj);
        }
      }

      /*
       * TODO(mquinn): Fill underlying record fields with data.
       */
      rs.next();

    } catch (final java.sql.SQLException sqle) {
      log.fatal(sqle.getMessage(), sqle);
      System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
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
   * Adds a page field to this record buffer.
   * @param ptRECNAME the RECNAME of the page field to add
   * @param ptFIELDNAME the FIELDNAME of the page field to add
   */
  public void addPageField(final String ptRECNAME,
      final String ptFIELDNAME) {
    RecordFieldBuffer f = this.fieldBufferTable.get(ptFIELDNAME);
    if (f == null) {
      f = new RecordFieldBuffer(ptRECNAME, ptFIELDNAME, this);
      this.fieldBufferTable.put(f.getFldName(), f);
      this.fieldBuffers.add(f);

      // Ensure this is done after adding to the table,
      // could cause infinte loop otherwise.
      f.checkFieldBufferRules();
    }
  }

  /**
   * Expand any and all subrecords for the underlying record
   * definition into this RecordBuffer.
   */
  public void expandEntireRecordIntoBuffer() {
    if (!this.hasBeenExpanded) {
      this.fieldBufferTable.clear();
      this.fieldBuffers.clear();
      final Record recDefn = DefnCache.getRecord(this.recName);
      final List<RecordField> expandedFieldList =
          recDefn.getExpandedFieldList();

      for (RecordField fld : expandedFieldList) {
        // Note: the true RECNAME is preserved in the FieldBuffer;
        // if the field is in a subrecord, the RECNAME in the
        // FieldBuffer will be that of the subrecord itself.
        final RecordFieldBuffer fldBuffer =
            new RecordFieldBuffer(fld.RECNAME, fld.FIELDNAME, this);
        this.fieldBufferTable.put(fldBuffer.getFldName(), fldBuffer);
        this.fieldBuffers.add(fldBuffer);
      }

      this.hasBeenExpanded = true;
    }
  }

  /**
   * Get the next buffer in the read sequence.
   * @return the next buffer in the read sequence.
   */
  public IStreamableBuffer next() {

    if (!this.hasEmittedSelf) {
      this.hasEmittedSelf = true;

      /*
       * The expansion routine returns fields in order, so sorting the
       * expanded array would mess up the proper order, since subrecord
       * fields would be interleaved with the parent's fields (order
       * is determined by FIELDNUM).
       */
      if (!this.hasBeenExpanded) {
        Collections.sort(this.fieldBuffers);
      }
      return this;
    }

    if (this.fieldBufferCursor < this.fieldBuffers.size()) {
      final RecordFieldBuffer fbuf =
          this.fieldBuffers.get(this.fieldBufferCursor);
      final IStreamableBuffer toRet = fbuf.next();
      if (toRet != null) {
        return toRet;
      } else {
        this.fieldBufferCursor++;
        return this.next();
      }
    }
    return null;
  }

  /**
   * Resets the read cursors for this buffer, and propagates
   * the call in order to recursively reset the read cursors for all child
   * buffers as well.
   */
  public void resetCursors() {

    this.hasEmittedSelf = false;
    this.fieldBufferCursor = 0;

    for (RecordFieldBuffer fbuf : this.fieldBuffers) {
      fbuf.resetCursors();
    }
  }
}


