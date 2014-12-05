/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.lang.reflect.Method;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.buffers.*;
import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.sql.*;
import org.openpplsoft.trace.*;

/**
 * Represents a PeopleTools rowset in the component buffer.
 */
public final class PTBufferRowset extends PTRowset implements ICBufferEntity {

  private static Logger log = LogManager.getLogger(
      PTBufferRowset.class.getName());

  private static Map<String, Method> ptMethodTable;

  // If this is null, this rowset is a standalone rowset.
  private ScrollBuffer cBufferScrollDefn;

  private Map<String, ScrollBuffer> registeredChildScrollDefns =
      new LinkedHashMap<String, ScrollBuffer>();

  static {
    final String PT_METHOD_PREFIX = "PT_";
    // cache pointers to PeopleTools Rowset methods.
    final Method[] methods = PTBufferRowset.class.getMethods();
    ptMethodTable = new HashMap<String, Method>();
    for (Method m : methods) {
      if (m.getName().indexOf(PT_METHOD_PREFIX) == 0) {
        ptMethodTable.put(m.getName().substring(
            PT_METHOD_PREFIX.length()), m);
      }
    }
  }

  /**
   * Remember: the provided primary record defn could be null if
   * this rowset represents the level 0 scroll of the component buffer.
   */
  public PTBufferRowset(final PTRowsetTypeConstraint origTc, final PTBufferRow pRow,
      final Record primRecDefn) {
    super(origTc);
    this.parentRow = pRow;
    this.primaryRecDefn = primRecDefn;
    this.initRowset();
  }

  public PTBufferRowset(final PTRowsetTypeConstraint origTc, final PTBufferRow pRow,
      final ScrollBuffer scrollDefn) {
    super(origTc);
    this.parentRow = pRow;
    this.cBufferScrollDefn = scrollDefn;
    this.primaryRecDefn = scrollDefn.getPrimaryRecDefn();
    this.initRowset();
  }

  private void initRowset() {
    // One row is always present in the rowset, even when flushed.
    this.rows.add(this.allocateNewRow());
    this.registerRecordDefn(this.primaryRecDefn);
  }

  protected PTRow allocateNewRow() {
    return new PTRowTypeConstraint().allocBufferRow(
        this, this.registeredRecordDefns, this.registeredChildScrollDefns);
  }

  public void registerRecordDefn(final Record recDefn) {

    if (recDefn == null) {
      return;
    }

    this.registeredRecordDefns.add(recDefn);

    // Each row must also have this record registered.
    for (final PTRow row : this.rows) {

      // If this is a component buffer scroll and the record has an
      // associated record buffer, pass that to the row; it will register
      // the underlying record defn and save a reference to that buffer
      if (this.cBufferScrollDefn != null
          && this.cBufferScrollDefn.hasRecordBuffer(recDefn.RECNAME)) {
        row.registerRecordDefn(
            this.cBufferScrollDefn.getRecordBuffer(recDefn.RECNAME));
      } else {
        row.registerRecordDefn(recDefn);
      }
    }
  }

  public void registerChildScrollDefn(final ScrollBuffer childScrollDefn) {
    if (this.registeredChildScrollDefns.containsKey(
        childScrollDefn.getPrimaryRecName())) {
      throw new OPSVMachRuntimeException("Halting on call to register child "
          + "scroll defn with a primary record name that has already been registerd; "
          + "registering it again would overwrite a potentially different defn.");
    } else {
      this.registeredChildScrollDefns.put(
          childScrollDefn.getPrimaryRecName(), childScrollDefn);
    }

    for (final PTRow row : this.rows) {
      row.registerChildScrollDefn(childScrollDefn);
    }
  }

  public void fireEvent(final PCEvent event,
      final FireEventSummary fireEventSummary) {
    for (final PTRow row : this.rows) {
      row.fireEvent(event, fireEventSummary);
    }
  }

  public PTRecord resolveContextualCBufferRecordReference(final String recName) {
    if (this.parentRow != null) {
      return this.parentRow.resolveContextualCBufferRecordReference(recName);
    }
    return null;
  }

  public PTReference<PTField> resolveContextualCBufferRecordFieldReference(
      final String recName, final String fieldName) {
    if (this.parentRow != null) {
      return this.parentRow.resolveContextualCBufferRecordFieldReference(
          recName, fieldName);
    }
    return null;
  }

  public PTRowset resolveContextualCBufferScrollReference(
      final PTScrollLiteral scrollName) {
    if (this.parentRow != null) {
      return this.parentRow.resolveContextualCBufferScrollReference(scrollName);
    }
    return null;
  }

  public void emitScrolls(final String indent) {
    for (int i = 0; i < this.rows.size(); i++) {
      this.rows.get(i).emitScrolls(indent);
    }
  }

  public void runFieldDefaultProcessing(
      final FieldDefaultProcSummary fldDefProcSummary) {
    for (final PTRow row : this.rows) {
      row.runFieldDefaultProcessing(fldDefProcSummary);
    }
  }

  public ScrollBuffer getCBufferScrollDefn() {
    return this.cBufferScrollDefn;
  }

  /**
   * If a key lookup request reaches a Rowset, the request should
   * always be passed to the parent row in order to look at the child
   * records within that row.
   */
  public void generateKeylist(
      final String fieldName, final Keylist keylist) {
    if (this.parentRow != null) {
      this.parentRow.generateKeylist(fieldName, keylist);
    }
  }

  public int getIndexOfRow(final PTRow row) {
    for (int i = 0; i < this.rows.size(); i++) {
      if(row == this.rows.get(i)) {
        return i;
      }
    }
    throw new OPSVMachRuntimeException("Unable to get index for the provided row; "
        + "row does not exist in this rowset.");
  }

  public int determineScrollLevel() {
    if (this == ComponentBuffer.getCBufferRowset()) {
      return -1;
    } else if (this.parentRow != null) {
      return 1 + this.parentRow.determineScrollLevel();
    } else {
      throw new OPSVMachRuntimeException("Unable to determine scroll level "
          + "for this rowset; this is not the root cbuffer rowset, but "
          + "the parent row is null, so unable to continue traversal.");
    }
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    if(this.primaryRecDefn == null) {
      b.append("!(CBUFFER-SCROLL-LEVEL-0-ROWSET)!");
    }
    b.append(":primaryRecDefn=").append(this.primaryRecDefn);
    b.append(",numRows=").append(this.rows.size());
    b.append(",registeredRecordDefns=").append(this.registeredRecordDefns);
    return b.toString();
  }
}
