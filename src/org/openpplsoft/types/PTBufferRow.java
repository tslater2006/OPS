/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.buffers.*;
import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.trace.*;

/**
 * Represents a PeopleTools row definition; contains
 * 1 to n child records and 0 to m child rowsets.
 */
public final class PTBufferRow extends PTRow<PTBufferRowset, PTBufferRecord>
    implements ICBufferEntity {

  private static Map<String, Method> ptMethodTable;

  private static final Logger log = LogManager.getLogger(PTBufferRow.class.getName());

  static {
    final String PT_METHOD_PREFIX = "PT_";
    // cache pointers to PeopleTools Row methods.
    final Method[] methods = PTBufferRow.class.getMethods();
    ptMethodTable = new HashMap<String, Method>();
    for (Method m : methods) {
      if (m.getName().indexOf(PT_METHOD_PREFIX) == 0) {
        ptMethodTable.put(m.getName().substring(
            PT_METHOD_PREFIX.length()), m);
      }
    }
  }

  public PTBufferRow(final PTRowTypeConstraint origTc, final PTBufferRowset pRowset,
      final Set<Record> recDefnsToRegister,
      final Map<String, ScrollBuffer> childScrollDefnsToRegister) {
    super(origTc);
    this.parentRowset = pRowset;

    // Register all record defns in the provided set.
    for(final Record recDefn : recDefnsToRegister) {
      this.registerRecordDefn(recDefn);
    }

    // Register all child scroll defns in the provided map.
    for(Map.Entry<String, ScrollBuffer> entry
        : childScrollDefnsToRegister.entrySet()) {
      this.registerChildScrollDefn(entry.getValue());
    }
  }

  /**
   * CAUTION: Do not rely on this as a way to get the
   * index for a record in all cases; this code does things
   * (i.e., excluding system records from index increment) that
   * may not apply to your needs.
   */
  public int getIndexPositionOfRecord(final PTBufferRecord rec) {
    int idxPos = 0;
    for (Map.Entry<String, PTBufferRecord> entry
        : this.recordMap.entrySet()) {
      if (entry.getValue() == rec) {
        return idxPos;
      }

      // System records are not given a numeric position
      // and thus should not cause the index counter to be incremented.
      if (!PSDefn.isSystemRecord(entry.getValue().getRecDefn().RECNAME)) {
        idxPos++;
      }
    }

    throw new OPSVMachRuntimeException("The provided record does "
        + "not exist in this row; unable to determine index position.");
  }

  // Used to register record defns that don't have an associated
  // buffer (i.e., standalone rows/rowsets)
  public void registerRecordDefn(final Record recDefn) {
    // Only register the record defn if it hasn't already been registered.
    if (!this.registeredRecordDefns.contains(recDefn)) {
      this.registeredRecordDefns.add(recDefn);
      this.recordMap.put(recDefn.RECNAME,
          new PTRecordTypeConstraint().allocBufferRecord(this, recDefn));
    }
  }

  // Used to register record defns that have an asscoiated buffer
  // (i.e., for records in the component buffer).
  public void registerRecordDefn(final RecordBuffer recBuffer) {

    final Record recDefn = recBuffer.getRecDefn();

    // Only register the record defn if it hasn't already been registered.
    if (!this.registeredRecordDefns.contains(recDefn)) {
      this.registeredRecordDefns.add(recDefn);
      this.recordMap.put(recDefn.RECNAME,
          new PTRecordTypeConstraint().allocBufferRecord(this, recBuffer));
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
      this.rowsetMap.put(childScrollDefn.getPrimaryRecName(),
          childScrollDefn.allocRowset(this));
    }
  }

  public void emitScrolls(final String indent) {

    int scrollLevel = Math.max(0, this.determineScrollLevel());
    TraceFileVerifier.submitEnforcedEmission(new BeginLevel(
        scrollLevel, this.getIndexOfThisRowInParentRowset(), 1, 1, 0,
        this.registeredChildScrollDefns.size(),
        // All non-level 0 rows seem to have these flags enabled.
        (scrollLevel != 0), (scrollLevel != 0),
        this.registeredRecordDefns.size()));

    for (Map.Entry<String, PTBufferRecord> entry : this.recordMap.entrySet()) {
      entry.getValue().emitRecInScroll();
    }

    for (Map.Entry<String, PTBufferRecord> entry : this.recordMap.entrySet()) {
      entry.getValue().emitScrolls(indent + "  ");
    }

    for (Map.Entry<String, PTBufferRowset> entry : this.rowsetMap.entrySet()) {
      entry.getValue().emitScrolls(indent + "  ");
    }
  }

  public void fireEvent(final PCEvent event,
      final FireEventSummary fireEventSummary) {

    // Fire event on each record in this row.
    for (Map.Entry<String, PTBufferRecord> entry : this.recordMap.entrySet()) {
      entry.getValue().fireEvent(event, fireEventSummary);
    }

    // Fire event on each rowset in this row.
    for (Map.Entry<String, PTBufferRowset> entry : this.rowsetMap.entrySet()) {
      entry.getValue().fireEvent(event, fireEventSummary);
    }
  }

  public void runFieldDefaultProcessing(
      final FieldDefaultProcSummary fldDefProcSummary) {

    // Run field default processing on each record in this row.
    for (Map.Entry<String, PTBufferRecord> entry : this.recordMap.entrySet()) {
      entry.getValue().runFieldDefaultProcessing(fldDefProcSummary);
    }

    // Run field default processing on each rowset in this row.
    for (Map.Entry<String, PTBufferRowset> entry : this.rowsetMap.entrySet()) {
      entry.getValue().runFieldDefaultProcessing(fldDefProcSummary);
    }
  }

  public PTBufferRecord resolveContextualCBufferRecordReference(final String recName) {
    if (this.recordMap.containsKey(recName)) {
      return this.recordMap.get(recName);
    } else if (this.parentRowset != null) {
      return this.parentRowset.resolveContextualCBufferRecordReference(recName);
    }
    return null;
  }

  public PTReference<PTBufferField> resolveContextualCBufferRecordFieldReference(
      final String recName, final String fieldName) {
    if (this.recordMap.containsKey(recName)
        && this.recordMap.get(recName).hasField(fieldName)) {
      return this.recordMap.get(recName).getFieldRef(fieldName);
    } else if (this.parentRowset != null) {
      return this.parentRowset.resolveContextualCBufferRecordFieldReference(
          recName, fieldName);
    }
    return null;
  }

  public PTBufferRowset resolveContextualCBufferScrollReference(
      final PTScrollLiteral scrollName) {
    if (this.rowsetMap.containsKey(scrollName.read())) {
      return this.rowsetMap.get(scrollName.read());
    } else if (this.parentRowset != null) {
      return this.parentRowset.resolveContextualCBufferScrollReference(scrollName);
    }
    return null;
  }

  public void generateKeylist(
      final String fieldName, final Keylist keylist) {

    // Can't continue if no parent rowset (scroll) exists.
    if (this.parentRowset == null) {
      return;
    }

    // If the parent rowset does not have a scroll defn, it may be the root
    // PTRowset representing the ComponentBuffer as a whole. If this is the case,
    // the entire search record (primary record for that rowset) should be searched.
    if (this.parentRowset.getCBufferScrollDefn() == null) {
      if (this.parentRowset == ComponentBuffer.getCBufferRowset()) {

        final PTBufferRecord searchRecord = this.recordMap.get(
            ComponentBuffer.getComponentDefn().getSearchRecordName());

        if (searchRecord.hasField(fieldName)) {
          final PTField fld = searchRecord.getFieldRef(fieldName).deref();
          keylist.add(fld);
        }
      } else {
        throw new OPSVMachRuntimeException("Parent rowset has null scroll defn, but it is not "
            + "of the root PTRowset object representing the component buffer as expected.");
      }
      return;
    }

    /*
     * We must access the buffer definitions, not the raw records themselves,
     * as not all record fields may be in the component buffer for any given record.
     */
    for (final RecordBuffer recBuf
        : this.parentRowset.getCBufferScrollDefn().getOrderedRecBuffers()) {
      final RecordFieldBuffer rfBuf = recBuf.getRecordFieldBuffer(fieldName);

      if (rfBuf != null) {
        final PTField fld = this.recordMap.get(
            rfBuf.getRecDefn().RECNAME).getFieldRef(rfBuf.getRecFldDefn()
                .FIELDNAME).deref();
        keylist.add(fld);
      }
    }

    this.parentRowset.generateKeylist(fieldName, keylist);
  }

  public int getIndexOfThisRowInParentRowset() {
    if (this.parentRowset != null) {
      return this.parentRowset.getIndexOfRow(this);
    }

    throw new OPSVMachRuntimeException("Unable to get index of this "
        + "row in parent rowset; parent rowset is null.");
  }

  public int determineScrollLevel() {
    if (this.parentRowset != null) {
      return this.parentRowset.determineScrollLevel();
    }

    throw new OPSVMachRuntimeException("Unable to determine scroll level "
        + "for this row; parent rowset is null.");
  }

  @Override
  public String toString() {
    return new StringBuilder(super.toString())
      .append(",childRecordRecDefns=").append(this.recordMap.keySet())
      .append(",childRowsetRecDefns=").append(this.rowsetMap.keySet())
      .toString();
  }
}
