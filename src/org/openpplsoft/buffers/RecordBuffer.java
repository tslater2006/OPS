/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.buffers;

import java.sql.*;
import java.util.*;
import java.lang.StringBuilder;
import org.openpplsoft.pt.*;
import org.openpplsoft.sql.*;
import org.openpplsoft.types.*;
import org.openpplsoft.runtime.*;
import org.apache.logging.log4j.*;

public class RecordBuffer implements IStreamableBuffer {

  public ScrollBuffer sbuf;
  public String recName;
  public int scrollLevel;
  public boolean isPrimaryScrollRecordBuffer;
  public HashMap<String, RecordFieldBuffer> fieldBufferTable;
  public ArrayList<RecordFieldBuffer> fieldBuffers;
  private boolean hasBeenExpanded;

  /*
   * IMPORTANT: I am using this to hold the actual data in the
   * buffer rather than store it in the individual field buffers.
   * At least for now, the RecordFieldBuffers are the authoritative source
   * on *which* fields are available in the component. The PTRecord
   * contains the data.
   */
  private PTRecord underlyingRecord;

  // Used for reading.
  private boolean hasEmittedSelf = false;
  private int fieldBufferCursor = 0;

  private Logger log = LogManager.getLogger(RecordBuffer.class.getName());

  public RecordBuffer(ScrollBuffer p, String r,
      int scrollLevel, String primaryRecName) {
    this.sbuf = p;
    this.recName = r;
    this.scrollLevel = scrollLevel;

    if(primaryRecName != null && this.recName.equals(primaryRecName)) {
      this.isPrimaryScrollRecordBuffer = true;
    }

    this.fieldBufferTable = new HashMap<String, RecordFieldBuffer>();
    this.fieldBuffers = new ArrayList<RecordFieldBuffer>();

    /*
     * TODO: This may not be the correct approach.
     * Not sure if EFFDT should always be in the component buffer, or if
     * it should only be there if based on the presence of specific keys.
     * This may even need to be broader than EFFDT; i.e., if any level-0 record
     * contains keys, add them now.
     */
    Record recDefn = DefnCache.getRecord(this.recName);
    RecordField EFFDT = recDefn.fieldTable.get("EFFDT");
    if(EFFDT != null && EFFDT.isKey()) {
      this.addPageField(this.recName, "EFFDT");
    }

    this.underlyingRecord = PTRecord.getSentinel().alloc(recDefn);
    }

  public void firstPassFill() {
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      pstmt = StmtLibrary.prepareFirstPassFillQuery(this);

      /*
       * If null comes back, one or more key values is not
       * available, and thus the fill cannot be run.
       */
      if(pstmt == null) { return; }

      rs = pstmt.executeQuery();

      ResultSetMetaData rsMetadata = rs.getMetaData();
      int numCols = rsMetadata.getColumnCount();

      if(rs.next()) { // record may legitimately be empty.
        Record recDefn = DefnCache.getRecord(this.recName);
        for(int i = 1; i <= numCols; i++) {
          String colName = rsMetadata.getColumnName(i);
          String colTypeName = rsMetadata.getColumnTypeName(i);
          PTField fldObj = this.underlyingRecord.getField(colName);
          log.debug("Before: {} = {}", colName, fldObj);
          GlobalFnLibrary.readFieldFromResultSet(fldObj,
              colName, colTypeName, rs);
          log.debug("After: {} = {}", colName, fldObj);
        }
      }
      rs.next();  // TODO: Fill underlying record fields with data.

    } catch(java.sql.SQLException sqle) {
      log.fatal(sqle.getMessage(), sqle);
      System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
    } finally {
      try {
        if(rs != null) { rs.close(); }
        if(pstmt != null) { pstmt.close(); }
      } catch(java.sql.SQLException sqle) {}
    }
  }

  public void addPageField(String RECNAME, String FIELDNAME) {
    RecordFieldBuffer f = this.fieldBufferTable.get(FIELDNAME);
    if(f == null) {
      f = new RecordFieldBuffer(RECNAME, FIELDNAME, this);
      this.fieldBufferTable.put(f.fldName, f);
      this.fieldBuffers.add(f);
      f.checkFieldBufferRules(); // Ensure this is done after adding to the table, could cause infinte loop ot$
    }
  }

  public void expandEntireRecordIntoBuffer() {

    if(!hasBeenExpanded) {
      fieldBufferTable.clear();
      fieldBuffers.clear();
      Record recDefn = DefnCache.getRecord(this.recName);
      ArrayList<RecordField> expandedFieldList = recDefn.getExpandedFieldList();

      for(RecordField fld : expandedFieldList) {
        // Note: the true RECNAME is preserved in the FieldBuffer; if the field is in a subrecord,
        // the RECNAME in the FieldBuffer will be that of the subrecord itself.
        RecordFieldBuffer fldBuffer = new RecordFieldBuffer(fld.RECNAME, fld.FIELDNAME, this);
        this.fieldBufferTable.put(fldBuffer.fldName, fldBuffer);
        this.fieldBuffers.add(fldBuffer);
      }

      hasBeenExpanded = true;
      }
    }

  public IStreamableBuffer next() {

    if(!this.hasEmittedSelf) {
      this.hasEmittedSelf = true;

      /*
       * The expansion routine returns fields in order, so sorting the
       * expanded array would mess up the proper order, since subrecord fields
       * would be interleaved with the parent's fields (order is determined by FIELDNUM).
       */
      if(!this.hasBeenExpanded) {
        Collections.sort(this.fieldBuffers);
      }
      return this;
    }

    if(this.fieldBufferCursor < this.fieldBuffers.size()) {
      RecordFieldBuffer fbuf = this.fieldBuffers.get(this.fieldBufferCursor);
      IStreamableBuffer toRet = fbuf.next();
      if(toRet != null) {
        return toRet;
      } else {
        this.fieldBufferCursor++;
        return this.next();
      }
    }
    return null;
  }

  public void resetCursors() {

    this.hasEmittedSelf = false;
    this.fieldBufferCursor = 0;

    for(RecordFieldBuffer fbuf : this.fieldBuffers) {
      fbuf.resetCursors();
    }
  }
}


