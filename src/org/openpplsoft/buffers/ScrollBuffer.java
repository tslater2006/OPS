/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.buffers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.openpplsoft.pt.*;
import org.openpplsoft.pt.pages.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.types.*;

/**
 * Represents a PeopleTools scroll buffer.
 */
public class ScrollBuffer implements IStreamableBuffer {

  private static Logger log = LogManager
      .getLogger(ScrollBuffer.class.getName());

  private int scrollLevel;
  private String primaryRecName;
  private ScrollBuffer parent;

  private int nextRecBufferOrderIdx = 0;

  private Map<String, ScrollBuffer> scrollBufferTable;
  private List<ScrollBuffer> orderedScrollBuffers;

  private Map<String, RecordBuffer> recBufferTable;
  private RelDisplayRecordSet relDisplayRecordSet;

  // this list is a total order on *ALL* records (non-reldisp and reldisp)
  private List<RecordBuffer> allRecBuffersOrdered;

  // Used for reading.
  private boolean hasEmittedSelf;
  private int recBufferCursor, scrollBufferCursor;

  /**
   * Constructs a scroll buffer with a particular scroll level, primary
   * record name, and parent scroll buffer.
   * @param l the scroll level of the new scroll buffer
   * @param r the primary record name of the new scroll buffer
   * @param p the parent scroll buffer of the new scroll buffer
   */
  public ScrollBuffer(final int l, final String r, final ScrollBuffer p) {
    this.scrollLevel = l;
    this.primaryRecName = r;
    this.parent = p;

    this.recBufferTable = new LinkedHashMap<String, RecordBuffer>();
    this.relDisplayRecordSet = new RelDisplayRecordSet();
    this.allRecBuffersOrdered = new ArrayList<RecordBuffer>();
    this.scrollBufferTable = new HashMap<String, ScrollBuffer>();
    this.orderedScrollBuffers = new ArrayList<ScrollBuffer>();
  }

  public Record getPrimaryRecDefn() {
    Record primaryRecDefn = null;
    if (this.primaryRecName != null) {
      primaryRecDefn = DefnCache.getRecord(this.primaryRecName);
    }
    return primaryRecDefn;
  }

  public PTBufferRowset allocRowset(final PTBufferRow parentRow) {

    // Create a rowset with the supplied parent; this scroll buffer object
    // will be linked to the rowset, which will register the record
    // and scroll buffers attached to this ScrollBuffer.
    return new PTRowsetTypeConstraint()
        .allocBufferRowset(parentRow, this);
  }

  /**
   * Get the scroll level on which this scroll buffer exists.
   * @return this scroll buffer's scroll level
   */
  public int getScrollLevel() {
    return this.scrollLevel;
  }

  /**
   * Get the primary record name for this scroll buffer.
   * @return the primary record name for this scroll buffer.
   */
  public String getPrimaryRecName() {
    return this.primaryRecName;
  }

  /**
   * Get this scroll buffer's parent scroll buffer.
   * @return this scroll buffer's parent scroll buffer
   */
  public ScrollBuffer getParentScrollBuffer() {
    return this.parent;
  }

  /**
   * Get the table that maps this scroll buffer's constituent
   * records to the records' corresponding record buffers.
   * @return a table mapping record names to record buffers
   */
  public Map<String, RecordBuffer> getRecBufferTable() {
    return this.recBufferTable;
  }

  public boolean hasRecordBuffer(final String recName) {
    return this.recBufferTable.containsKey(recName);
  }

  public RecordBuffer getRecordBuffer(final String recName) {
    return this.recBufferTable.get(recName);
  }

  /**
   * Add a page field to this scroll buffer.
   * @param tok the page field token representing the page field
   *    to be added.
   */
  public void addPageField(final PgToken tok) {

    RecordBuffer r = null;
    if (tok.isRelatedDisplay()) {

      final String dispCtrlRecFldName = tok.getDispControlRecFieldName();
      log.debug("Adding page field for reldisp token {}; disp ctrl fld "
          + "name is {}", tok, dispCtrlRecFldName);

      if (this.relDisplayRecordSet
          .hasRecord(dispCtrlRecFldName, tok.getRecName())) {
        r = this.relDisplayRecordSet
          .getRecordBuffer(dispCtrlRecFldName, tok.getRecName());
        log.debug("Using preexisting rel display record buffer: {}", r);
      } else {
        r = new RecordBuffer(this, this.nextRecBufferOrderIdx++, tok.getRecName());
        this.relDisplayRecordSet.registerRecord(dispCtrlRecFldName, r);
        this.allRecBuffersOrdered.add(r);
        log.debug("Created new rel display record buffer: {}", r);
      }
    } else {
      r = this.recBufferTable.get(tok.getRecName());
      if (r == null) {
        r = new RecordBuffer(this, this.nextRecBufferOrderIdx++, tok.getRecName());
        this.recBufferTable.put(r.getRecDefn().RECNAME, r);
        this.allRecBuffersOrdered.add(r);
      }
    }

    r.addPageField(tok.getRecName(), tok.getFldName(), tok);
  }

  /**
   * Gets a child scroll buffer given the name of the primary record
   * belonging to the desired child scroll buffer.
   * @param targetPrimaryRecName the primary record name attached to the
   *    desired scroll
   * @return the child scroll buffer with a primary record name matching
   *    the provided record name
   */
  public ScrollBuffer getChildScroll(final String targetPrimaryRecName) {
    ScrollBuffer sb = this.scrollBufferTable.get(targetPrimaryRecName);
    if (sb == null) {
      sb = new ScrollBuffer(this.scrollLevel + 1, targetPrimaryRecName, this);
      this.scrollBufferTable.put(targetPrimaryRecName, sb);
      this.orderedScrollBuffers.add(sb);
    }
    return sb;
  }

  public List<RecordBuffer> getOrderedNonRelDispRecBuffers() {
    final List<RecordBuffer> orderedNonRelDispRecBuffers =
        new ArrayList<RecordBuffer>();
    for (final Map.Entry<String, RecordBuffer> entry
        : this.recBufferTable.entrySet()) {
      orderedNonRelDispRecBuffers.add(entry.getValue());
    }
    return orderedNonRelDispRecBuffers;
  }

  public List<ScrollBuffer> getOrderedScrollBuffers() {
    return this.orderedScrollBuffers;
  }

  public RelDisplayRecordSet getRelDisplayRecordSet() {
    return this.relDisplayRecordSet;
  }

  /**
   * Gets the next child buffer in the read sequence.
   * @return the next child buffer in the read sequence
   */
  public IStreamableBuffer next() {

    if (!this.hasEmittedSelf) {
      this.hasEmittedSelf = true;
      return this;
    }

    if (this.recBufferCursor < this.allRecBuffersOrdered.size()) {
      final RecordBuffer rbuf =
          this.allRecBuffersOrdered.get(this.recBufferCursor);
      final IStreamableBuffer toRet = rbuf.next();
      if (toRet != null) {
        return toRet;
      } else {
        this.recBufferCursor++;
        return this.next();
      }
    }

    if (this.scrollBufferCursor < this.orderedScrollBuffers.size()) {
      final ScrollBuffer sbuf =
          this.orderedScrollBuffers.get(this.scrollBufferCursor);
      final IStreamableBuffer toRet = sbuf.next();
      if (toRet != null) {
        return toRet;
      } else {
        this.scrollBufferCursor++;
        return this.next();
      }
    }

    return null;
  }

  /**
   * Resets the read cursors on this, and (recursively) on all child
   * buffers.
   */
  public void resetCursors() {

    this.hasEmittedSelf = false;
    this.recBufferCursor = 0;
    this.scrollBufferCursor = 0;

    for (RecordBuffer rbuf : this.allRecBuffersOrdered) {
      rbuf.resetCursors();
    }

    for (ScrollBuffer sbuf : this.orderedScrollBuffers) {
      sbuf.resetCursors();
    }
  }
}
