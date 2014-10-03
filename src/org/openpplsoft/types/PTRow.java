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

/**
 * Represents a PeopleTools row definition; contains
 * 1 to n child records and 0 to m child rowsets.
 */
public final class PTRow extends PTObjectType implements ICBufferEntity {

  private static Map<String, Method> ptMethodTable;

  private static final Logger log = LogManager.getLogger(PTRow.class.getName());

  private PTRowset parentRowset;

  // Maps record names to child record objects
  private Map<String, PTRecord> recordMap = new LinkedHashMap<String, PTRecord>();
  private Set<Record> registeredRecordDefns = new HashSet<Record>();

  // Maps scroll/rowset primary rec names to rowset objects
  private Map<String, PTRowset> rowsetMap = new LinkedHashMap<String, PTRowset>();
  private Map<String, ScrollBuffer> registeredChildScrollDefns =
      new LinkedHashMap<String, ScrollBuffer>();

  static {
    final String PT_METHOD_PREFIX = "PT_";
    // cache pointers to PeopleTools Row methods.
    final Method[] methods = PTRow.class.getMethods();
    ptMethodTable = new HashMap<String, Method>();
    for (Method m : methods) {
      if (m.getName().indexOf(PT_METHOD_PREFIX) == 0) {
        ptMethodTable.put(m.getName().substring(
            PT_METHOD_PREFIX.length()), m);
      }
    }
  }

  public PTRow(final PTRowTypeConstraint origTc, final PTRowset pRowset,
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

  // Used to register record defns that don't have an associated
  // buffer (i.e., standalone rows/rowsets)
  public void registerRecordDefn(final Record recDefn) {
    // Only register the record defn if it hasn't already been registered.
    if (!this.registeredRecordDefns.contains(recDefn)) {
      this.registeredRecordDefns.add(recDefn);
      this.recordMap.put(recDefn.RECNAME,
          new PTRecordTypeConstraint().alloc(this, recDefn));
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
          new PTRecordTypeConstraint().alloc(this, recBuffer));
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

  public void fireEvent(final PCEvent event,
      final FireEventSummary fireEventSummary) {

    // Fire event on each record in this row.
    for (Map.Entry<String, PTRecord> entry : this.recordMap.entrySet()) {
      entry.getValue().fireEvent(event, fireEventSummary);
    }

    // Fire event on each rowset in this row.
    for (Map.Entry<String, PTRowset> entry : this.rowsetMap.entrySet()) {
      entry.getValue().fireEvent(event, fireEventSummary);
    }
  }

  public void runFieldDefaultProcessing(
      final FieldDefaultProcSummary fldDefProcSummary) {

    // Run field default processing on each record in this row.
    for (Map.Entry<String, PTRecord> entry : this.recordMap.entrySet()) {
      entry.getValue().runFieldDefaultProcessing(fldDefProcSummary);
    }

    // Run field default processing on each rowset in this row.
    for (Map.Entry<String, PTRowset> entry : this.rowsetMap.entrySet()) {
      entry.getValue().runFieldDefaultProcessing(fldDefProcSummary);
    }
  }

  public PTType resolveContextualCBufferReference(final String identifier) {
    if (this.recordMap.containsKey(identifier)) {
      return this.recordMap.get(identifier);
    } else if (this.parentRowset != null) {
      return this.parentRowset.resolveContextualCBufferReference(identifier);
    }
    return null;
  }

  public PTPrimitiveType findValueForKeyInCBufferContext(
      final String fieldName) throws OPSCBufferKeyLookupException {

    if (this.parentRowset == null) {
      throw new OPSCBufferKeyLookupException("Row's parent is null; "
          + "unable to continue search for key value without a parent rowset.");
    }

    // If the parent rowset does not have a scroll defn, it may be the root
    // PTRowset representing the ComponentBuffer as a whole. If this is the case,
    // the entire search record (primary record for that rowset) should be searched.
    if (this.parentRowset.getCBufferScrollDefn() == null) {
      if (this.parentRowset == ComponentBuffer.getCBufferRowset()) {

        final PTRecord searchRecord = this.recordMap.get(
            ComponentBuffer.getComponentDefn().getSearchRecordName());

        if (searchRecord.hasField(fieldName)) {
          final PTField fld = searchRecord.getFieldRef(fieldName).deref();
          if (fld.getRecordFieldDefn().isKey()) {
            return fld.getValue();
          }
        }

        throw new OPSCBufferKeyLookupException("Key lookup extended all the way to "
            + "component search record, value for key was not found anywhere.");
      }
      throw new OPSCBufferKeyLookupException("Row's parent rowset does not "
          + "have a scroll defn, and it is not the ComponentBuffer rowset; "
          + "these are unexpected conditions that prevent further key lookup.");
    }

    /*
     * We must access the buffer definitions, not the raw records themselves,
     * as not all record fields may be in the component buffer for any given record.
     */
    for (final RecordBuffer recBuf
        : this.parentRowset.getCBufferScrollDefn().getOrderedRecBuffers()) {
      final RecordFieldBuffer rfBuf = recBuf.getRecordFieldBuffer(fieldName);

      if (rfBuf != null && rfBuf.getRecFldDefn().isKey()) {
        return this.recordMap.get(rfBuf.getRecDefn().RECNAME)
            .getFieldRef(rfBuf.getRecFldDefn().FIELDNAME).deref().getValue();
      }
    }

    // If the field does not exist, and/or is not a key, in/on any of the
    // records in this row, pass the field name to the parent rowset
    // to continue the search in the next parent row.
    return this.parentRowset.findValueForKeyInCBufferContext(fieldName);
  }

  public PTRowset getParentRowset() {
    return this.parentRowset;
  }

  /**
   * Retrieve the record associated with the record name provided
   * @return the record associated with the record name provided
   */
  public PTRecord getRecord(final String recName) {
    return this.recordMap.get(recName);
  }

  public PTRowset getRowset(final String primaryRecName) {
    return this.rowsetMap.get(primaryRecName);
  }

  /**
   * Determines if the given record exists in the row.
   * @return true if record exists, false otherwise
   */
  public boolean hasRecord(final String recName) {
    return this.recordMap.containsKey(recName);
  }

  /**
   * Implementation of GetRecord method for the PeopleTools
   * row class.
   */
  public void PT_GetRecord() {
    final List<PTType> args = Environment.getArgsFromCallStack();
    if (args.size() != 1) {
      throw new OPSVMachRuntimeException("Expected only one arg.");
    }

    PTRecord rec = null;
    if(args.get(0) instanceof PTRecordLiteral) {
      rec = this.getRecord(((PTRecordLiteral) args.get(0)).getRecName());
    } else if (args.get(0) instanceof PTString) {
      rec = this.getRecord(((PTString) args.get(0)).read());
    } else {
      throw new OPSVMachRuntimeException("Expected arg to GetRecord() to "
          + "be a PTRecordLiteral or PTString.");
    }

    Environment.pushToCallStack(rec);
  }

  @Override
  public PTType dotProperty(final String s) {
    if (this.recordMap.containsKey(s)) {
      return this.recordMap.get(s);
    }
    return null;
  }

  @Override
  public Callable dotMethod(final String s) {
    if (ptMethodTable.containsKey(s)) {
      return new Callable(ptMethodTable.get(s), this);
    }
    return null;
  }

  public Map<String, PTRecord> getRecordMap() {
    return this.recordMap;
  }

  @Override
  public void setReadOnly() {
    super.setReadOnly();

    // Calls to make a row read-only must make its child records read-only.
    for(Map.Entry<String, PTRecord> cursor: this.recordMap.entrySet()) {
      cursor.getValue().setReadOnly();
    }

    // Calls to make a row read-only must make its child rowsets read-only.
    for(Map.Entry<String, PTRowset> cursor: this.rowsetMap.entrySet()) {
      cursor.getValue().setReadOnly();
    }
  }

  @Override
  public String toString() {
    return new StringBuilder(super.toString())
      .append(",childRecordRecDefns=").append(this.recordMap.keySet())
      .append(",childRowsetRecDefns=").append(this.rowsetMap.keySet())
      .toString();
  }
}
