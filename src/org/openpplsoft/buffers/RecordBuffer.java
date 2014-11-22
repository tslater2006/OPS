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
import org.openpplsoft.pt.pages.*;
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
  private Record recDefn;
  private int scrollLevel;
  private boolean isPrimaryScrollRecordBuffer, hasBeenExpanded;
  private Map<String, RecordFieldBuffer> fieldBufferTable;
  private List<RecordFieldBuffer> fieldBuffers;

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
    this.recDefn = DefnCache.getRecord(this.recName);
  }

  /**
   * Get the name of the record underlying this buffer.
   * @return the RECNAME of the underlying record
   */
  public String getRecName() {
    return this.recName;
  }

  public Record getRecDefn() {
    return this.recDefn;
  }

  /**
   * Get the scroll level at which this record buffer exists.
   * @return the scroll level at which this record buffer exists
   */
  public int getScrollLevel() {
    return this.scrollLevel;
  }

  public RecordFieldBuffer getRecordFieldBuffer(final String fieldname) {
    return this.fieldBufferTable.get(fieldname);
  }

  public boolean hasRecordFieldBuffer(final String fieldName) {
    return this.fieldBufferTable.containsKey(fieldName);
  }

  public void addEffDtKeyIfNecessary() {
    final RecordField EFFDT = this.recDefn.fieldTable.get("EFFDT");
    if (this.recDefn.isDerivedWorkRecord() && EFFDT != null && EFFDT.isKey()) {
      this.addPageField(this.recName, "EFFDT", null);
    }
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
   * Adds a page field to this record buffer.
   * @param ptRECNAME the RECNAME of the page field to add
   * @param ptFIELDNAME the FIELDNAME of the page field to add
   */
  public void addPageField(final String ptRECNAME,
      final String ptFIELDNAME, final PgToken srcTok) {

    if (!ptRECNAME.equals(this.recName)) {
      throw new OPSVMachRuntimeException("Illegal attempt to add page "
        + "field " + ptRECNAME + "." + ptFIELDNAME + " ("
        + srcTok + ") to record with different name"
        + this.recName);
    }

    RecordFieldBuffer f = this.fieldBufferTable.get(ptFIELDNAME);
    if (f == null) {
      f = new RecordFieldBuffer(ptRECNAME, ptFIELDNAME, this, srcTok);
      this.fieldBufferTable.put(f.getFldName(), f);
      this.fieldBuffers.add(f);
    }
  }

  public List<RecordFieldBuffer> getFieldBuffers() {
    return this.fieldBuffers;
  }

  /**
   * Expand any and all subrecords for the underlying record
   * definition into this RecordBuffer.
   */
  public void expandEntireRecordIntoBuffer() {
    if (!this.hasBeenExpanded) {

      final Map<String, RecordFieldBuffer> newTable =
          new HashMap<String, RecordFieldBuffer>();
      final List<RecordFieldBuffer> newList =
          new ArrayList<RecordFieldBuffer>();

      final Record recDefn = DefnCache.getRecord(this.recName);
      final List<RecordField> expandedFieldList =
          recDefn.getExpandedFieldList();

      for (RecordField fld : expandedFieldList) {
        // Note: the true RECNAME is preserved in the FieldBuffer;
        // if the field is in a subrecord, the RECNAME in the
        // FieldBuffer will be that of the subrecord itself.

        PgToken srcPageToken = null;
        if (this.fieldBufferTable.containsKey(fld.FIELDNAME)) {
          srcPageToken = this.fieldBufferTable.get(fld.FIELDNAME).getSrcPageToken();
        }

        final RecordFieldBuffer fldBuffer =
            new RecordFieldBuffer(fld.RECNAME, fld.FIELDNAME, this, srcPageToken);
        newTable.put(fldBuffer.getFldName(), fldBuffer);
        newList.add(fldBuffer);
      }

      this.fieldBufferTable = newTable;
      this.fieldBuffers = newList;
      this.hasBeenExpanded = true;
    }
  }

  /**
   * There is no concept of a "related display record"
   * in PeopleSoft; this is the name I'm giving to
   * records in the component buffer that contain only
   * a single related field buffer.
   */
  public boolean isRelatedDisplayRecBuffer() {
    return this.fieldBuffers.size() == 1
        && this.fieldBuffers.get(0).getSrcPageToken() != null
        && this.fieldBuffers.get(0).getSrcPageToken().isRelatedDisplay();
  }

  public boolean doesContainStructuralFields() {

/*    log.debug("Begin fields for record: {}", this.recName);
    for (final RecordFieldBuffer buf : this.fieldBuffers) {
      log.debug("{} | {}", buf.getFldName(), buf.getSrcPageToken());
    }
    log.debug("End fields for record: {}", this.recName);*/

    for (final RecordFieldBuffer buf : this.fieldBuffers) {
      if (!buf.isRelatedDisplayField()) {
        return true;
      }
    }
    return false;
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


