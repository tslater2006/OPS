/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.buffers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.openpplsoft.pt.*;
import org.openpplsoft.pt.pages.*;
import org.openpplsoft.runtime.*;

/**
 * Represents a PeopleTools record field buffer.
 */
public class RecordFieldBuffer implements IStreamableBuffer,
    Comparable<RecordFieldBuffer> {

  private static Logger log =
      LogManager.getLogger(RecordFieldBuffer.class.getName());

  private String fldName;
  private Record recDefn;
  private RecordField fldDefn;
  private RecordBuffer parentRecordBuffer;

  /*
   * A record field buffer is usually associated with one or more
   * page field tokens (a page field token represents a page field
   * that references/displays/uses the underlying record field's value
   * in some way). Keep in mind though that this list can be empty
   * (i.e., record field buffers that are automatically inserted during
   * buffer assembly).
   */
  private List<PgToken> pageFieldToks;

  // Used for reading.
  private boolean hasEmittedSelf;

  public RecordFieldBuffer(final String r, final String f,
      final RecordBuffer parent, final PgToken tok) {
    this.fldName = f;
    this.recDefn = DefnCache.getRecord(r);
    this.parentRecordBuffer = parent;

    this.pageFieldToks = new ArrayList<PgToken>();
    if (tok != null) {
      this.pageFieldToks.add(tok);
    }

    if (this.recDefn.hasField(this.fldName)) {
      this.fldDefn = this.recDefn.fieldTable.get(this.fldName);
    } else {
      boolean wasFieldFoundOnSubrecord = false;
      for (final String subrecName : this.recDefn.getSubrecordNames()) {
        final Record subRecDefn = DefnCache.getRecord(subrecName);
        if (subRecDefn.hasField(this.fldName)) {
          this.fldDefn = subRecDefn.fieldTable.get(this.fldName);
          wasFieldFoundOnSubrecord = true;
          break;
        }
      }
      if (!wasFieldFoundOnSubrecord) {
        throw new OPSVMachRuntimeException("Field defn was not found "
            + "in recDefn or any of its subrecord defns.");
      }
    }
  }

  public List<PgToken> getPageFieldToks() {
    return this.pageFieldToks;
  }

  public PgToken getOnlyPageFieldTok() {
    if (this.pageFieldToks.size() != 1) {
      throw new OPSVMachRuntimeException("Illegal call to getOnlyPageFieldTok(); "
          + "this record field buffer has 0 or multiple page field tokens: "
          + this.pageFieldToks);
    }
    return this.pageFieldToks.get(0);
  }

  public void addPageFieldTok(final PgToken pgFieldTok) {
    this.pageFieldToks.add(pgFieldTok);
  }

  public boolean hasPageFieldTok(final PgToken pgFieldTok) {
    return this.pageFieldToks.contains(pgFieldTok);
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

  public boolean isRelatedDisplayField() {

    final Optional<Boolean> result = this.pageFieldToks.stream()
        .map(PgToken::isRelatedDisplay)
        .reduce((accum, b) -> {
            if (accum != b) {
              throw new OPSVMachRuntimeException("While determining if record field "
                  + "buffer is related display, encountered a combination of nonreldisp "
                  + "page field tokens and reldisp page field tokens.");
            }
            return accum && b;
        });

    return result.orElse(false);
  }

  public boolean isDisplayControlField() {

    final Optional<Boolean> result = this.pageFieldToks.stream()
        .map(PgToken::isDisplayControl)
        .reduce((accum, b) -> {
            if (accum != b) {
              throw new OPSVMachRuntimeException("While determining if record field "
                  + "buffer is display control, encountered a combination of nondispctrl "
                  + "page field tokens and dispctrl page field tokens.");
            }
            return accum && b;
        });

    return result.orElse(false);
  }

  public void expandParentRecordBufferIfNecessary() {
    /*
     * If a level 0, non-derived record contains at least one field
     * that is neither a search key nor an alternate key, and if the
     * record has at least one structural field, all of the
     * record's fields should be present in the component buffer.
     */
    if (this.parentRecordBuffer.getScrollLevel() == 0
        && this.parentRecordBuffer.doesContainStructuralFields()
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

  @Override
  public String toString() {
    return "RecordFieldBuffer: fldName=" + this.fldName
        + ", pageFieldToks=" + this.pageFieldToks;
  }
}

