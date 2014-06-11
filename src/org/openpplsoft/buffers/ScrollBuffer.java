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
import org.openpplsoft.pt.pages.*;
import org.openpplsoft.types.*;
import org.openpplsoft.runtime.*;

public class ScrollBuffer implements IStreamableBuffer {

  public int scrollLevel;
  public String primaryRecName;
  public ScrollBuffer parent;
  public HashMap<String, RecordBuffer> recBufferTable;
  public ArrayList<RecordBuffer> orderedRecBuffers;

  /*
   * TODO: I need to figure out how I'm going to integrate this
   * with the buffer objects/concepts I've written to-date. Punting
   * for now.
   */
  public Map<String, PTRecord> recEntryTable;

  public HashMap<String, ScrollBuffer> scrollBufferTable;
  public ArrayList<ScrollBuffer> orderedScrollBuffers;

  // Used for reading.
  private boolean hasEmittedSelf = false;
  private int recBufferCursor = 0;
  private int scrollBufferCursor = 0;

  public ScrollBuffer(int level, String primaryRecName, ScrollBuffer parent) {
    this.scrollLevel = level;
    this.primaryRecName = primaryRecName;
    this.parent = parent;

    this.recBufferTable = new HashMap<String, RecordBuffer>();
    this.orderedRecBuffers = new ArrayList<RecordBuffer>();
    this.scrollBufferTable = new HashMap<String, ScrollBuffer>();
    this.orderedScrollBuffers = new ArrayList<ScrollBuffer>();
    this.recEntryTable = new HashMap<String, PTRecord>();
  }

  public PTPrimitiveType getKeyValueFromHierarchy(String fldName) {
    if(this.parent == null) {
      /*
       * Examine the search record of the component buffer
       * if this is the level 0 scroll buffer.
       */
      if(ComponentBuffer.getSearchRecord().hasField(fldName)) {
        return ComponentBuffer.getSearchRecord().getField(fldName)
            .getValue();
      }
    } else {
      /*
       * Examine the primary scroll record of the *parent* scroll
       * buffer, not this scroll buffer. If key value exists,
       * return it, otherwise call this method on the parent's parent
       * scroll buffer.
       */
      throw new OPSVMachRuntimeException("Need to support getting "+
        "key values from scroll 1 and/or 2 of comp buffer.");
    }
    return null;
  }

  public void addPageField(PgToken tok) {
    RecordBuffer r = this.recBufferTable.get(tok.RECNAME);
    if(r == null) {
      r = new RecordBuffer(this, tok.RECNAME, this.scrollLevel, this.primaryRecName);
      this.recBufferTable.put(r.getRecName(), r);
      orderedRecBuffers.add(r);
    }
    r.addPageField(tok.RECNAME, tok.FIELDNAME);
  }

  public ScrollBuffer getChildScroll(String targetPrimaryRecName) {
    ScrollBuffer sb = this.scrollBufferTable.get(targetPrimaryRecName);
    if(sb == null) {
      sb = new ScrollBuffer(this.scrollLevel + 1, targetPrimaryRecName, this);
      this.scrollBufferTable.put(targetPrimaryRecName, sb);
      this.orderedScrollBuffers.add(sb);
    }
    return sb;
  }

  public IStreamableBuffer next() {

    if(!this.hasEmittedSelf) {
      this.hasEmittedSelf = true;
      return this;
    }

    if(this.recBufferCursor < this.orderedRecBuffers.size()) {
      RecordBuffer rbuf = this.orderedRecBuffers.get(this.recBufferCursor);
      IStreamableBuffer toRet = rbuf.next();
      if(toRet != null) {
        return toRet;
      } else {
        this.recBufferCursor++;
        return this.next();
      }
    }

    if(this.scrollBufferCursor < this.orderedScrollBuffers.size()) {
      ScrollBuffer sbuf = this.orderedScrollBuffers.get(this.scrollBufferCursor);
      IStreamableBuffer toRet = sbuf.next();
      if(toRet != null) {
        return toRet;
      } else {
        this.scrollBufferCursor++;
        return this.next();
      }
    }

    return null;
  }

  public void resetCursors() {

    this.hasEmittedSelf = false;
    this.recBufferCursor = 0;
    this.scrollBufferCursor = 0;

    for(RecordBuffer rbuf : this.orderedRecBuffers) {
      rbuf.resetCursors();
    }

    for(ScrollBuffer sbuf : this.orderedScrollBuffers) {
      sbuf.resetCursors();
    }
  }
}
