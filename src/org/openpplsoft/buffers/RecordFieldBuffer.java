/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.buffers;

import java.util.*;
import java.lang.StringBuilder;
import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;

public class RecordFieldBuffer implements IStreamableBuffer,
    Comparable<RecordFieldBuffer> {

  public String fldName;
  public Record recDefn;
  public RecordField fldDefn;
  public RecordBuffer parentRecordBuffer;

  // Used for reading.
  private boolean hasEmittedSelf = false;

  public RecordFieldBuffer(String r, String f, RecordBuffer parent) {
    this.fldName = f;
    this.recDefn = DefnCache.getRecord(r);
    this.fldDefn = this.recDefn.fieldTable.get(this.fldName);
    this.parentRecordBuffer = parent;

    if(this.fldDefn == null) {
      throw new EntVMachRuntimeException("Field not found on the record supplied. Likely on a subrecord. " +
          "Subrecord traversal in RecordFieldBuffer not supported at this time.");
    }
  }

  public void checkFieldBufferRules() {

    /*
     * If a level 0, non-derived record contains at least one field
     * that is neither a search key nor an alternate key, all of the record's fields
     * should be present in the component buffer.
     */
    if(this.parentRecordBuffer.scrollLevel == 0
        && !this.recDefn.isDerivedWorkRecord()
        && !this.fldDefn.isAlternateSearchKey()) {
      this.parentRecordBuffer.expandEntireRecordIntoBuffer();
    }

    /*
     * All the fields on a primary scroll record at level 1 or higher
     * should be present in the component buffer.
     */
    if(this.parentRecordBuffer.scrollLevel > 0
        && this.parentRecordBuffer.isPrimaryScrollRecordBuffer) {
      this.parentRecordBuffer.expandEntireRecordIntoBuffer();
    }
  }

  public int compareTo(RecordFieldBuffer fb) {
    int a = this.fldDefn.FIELDNUM;
    int b = fb.fldDefn.FIELDNUM;
    return a > b ? +1 : a < b ? -1 : 0;
  }

  public IStreamableBuffer next() {

    if(!this.hasEmittedSelf) {
      this.hasEmittedSelf = true;
      return this;
    }

    return null;
  }

  public void resetCursors() {
    this.hasEmittedSelf = false;
  }
}

