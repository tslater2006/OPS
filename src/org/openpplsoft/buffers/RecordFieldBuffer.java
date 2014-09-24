/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.buffers;

import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;

/**
 * Represents a PeopleTools record field buffer.
 */
public class RecordFieldBuffer implements IStreamableBuffer,
    Comparable<RecordFieldBuffer> {

  private String fldName;
  private Record recDefn;
  private RecordField fldDefn;
  private RecordBuffer parentRecordBuffer;

  // Used for reading.
  private boolean hasEmittedSelf;

  /**
   * Creates a buffer for a record field.
   * @param r the RECNAME of the record field
   * @param f the FLDNAME of the record field
   * @param parent the field's parent RecordBuffer
   */
  public RecordFieldBuffer(final String r, final String f,
      final RecordBuffer parent) {
    this.fldName = f;
    this.recDefn = DefnCache.getRecord(r);
    this.fldDefn = this.recDefn.fieldTable.get(this.fldName);
    this.parentRecordBuffer = parent;

    if (this.fldDefn == null) {
      throw new OPSVMachRuntimeException("Field not found on the record "
          + "supplied. Likely on a subrecord. Subrecord traversal in "
          + "RecordFieldBuffer not supported at this time.");
    }
  }

  public RecordBuffer getParentRecordBuffer() {
    return this.parentRecordBuffer;
  }

  /**
   * Retrieves the FLDNAME for the underlying record field.
   * @return the underlying record's FLDNAME
   */
  public String getFldName() {
    return this.fldName;
  }

  public Record getRecDefn() {
    return this.recDefn;
  }

  public RecordField getRecFldDefn() {
    return this.fldDefn;
  }

  /**
   * Expands the entire parent record into the parent
   * record buffer based on PeopleTools logic.
   */
  public void checkFieldBufferRules() {
    /*
     * If a level 0, non-derived record contains at least one field
     * that is neither a search key nor an alternate key, all of the
     * record's fields should be present in the component buffer.
     */
    if (this.parentRecordBuffer.getScrollLevel() == 0
        && !this.recDefn.isDerivedWorkRecord()
        && !this.fldDefn.isAlternateSearchKey()) {
      this.parentRecordBuffer.expandEntireRecordIntoBuffer();
    }

    /*
     * All the fields on a primary scroll record at level 1 or higher
     * should be present in the component buffer.
     */
    if (this.parentRecordBuffer.getScrollLevel() > 0
        && this.parentRecordBuffer.getIsPrimaryScrollRecordBuffer()) {
      this.parentRecordBuffer.expandEntireRecordIntoBuffer();
    }
  }

  /**
   * RecordFieldBuffers are sorted by ascending FIELDNUM.
   * @param fb the RecordFieldBuffer to compare this instance
   *    against
   * @return -1 if this buffer comes before {@code fb}, +1 if
   *    this buffer comes after, 0 otherwise.
   */
  public int compareTo(final RecordFieldBuffer fb) {
    final int a = this.fldDefn.FIELDNUM;
    final int b = fb.fldDefn.FIELDNUM;
    return a > b ? +1 : a < b ? -1 : 0;
  }

  /**
   * Retrieves the next buffer in the read sequence.
   * @return the next buffer in the read sequence
   */
  public IStreamableBuffer next() {
    if (!this.hasEmittedSelf) {
      this.hasEmittedSelf = true;
      return this;
    }
    return null;
  }

  /**
   * Resets read cursors, and propagates this call
   * recursively to child buffers.
   */
  public void resetCursors() {
    this.hasEmittedSelf = false;
  }
}

