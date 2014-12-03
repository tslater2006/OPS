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
public abstract class PTRow extends PTObjectType {

  private static Map<String, Method> ptMethodTable;

  private static final Logger log = LogManager.getLogger(PTRow.class.getName());

  protected PTRowset parentRowset;
  protected PTImmutableReference<PTBoolean> selectedPropertyRef;

  // Maps record names to child record objects
  protected Map<String, PTRecord> recordMap = new LinkedHashMap<String, PTRecord>();
  protected Set<Record> registeredRecordDefns = new HashSet<Record>();

  // Maps scroll/rowset primary rec names to rowset objects
  protected Map<String, PTRowset> rowsetMap = new LinkedHashMap<String, PTRowset>();
  protected Map<String, ScrollBuffer> registeredChildScrollDefns =
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

  public PTRow(final PTRowTypeConstraint origTc) {
    super(origTc);
  }

  public abstract void generateKeylist(String fieldName, Keylist keylist);
  public abstract void emitScrolls(String indent);
  public abstract void fireEvent(PCEvent event, FireEventSummary fireEventSummary);
  public abstract void runFieldDefaultProcessing(FieldDefaultProcSummary fldDefProcSummary);
  public abstract PTRowset resolveContextualCBufferScrollReference(PTScrollLiteral scrollName);
  public abstract PTRecord resolveContextualCBufferRecordReference(String recName);
  public abstract PTReference<PTField> resolveContextualCBufferRecordFieldReference(String recName, String fldName);
  public abstract int determineScrollLevel();
  public abstract void registerRecordDefn(Record rec);
  public abstract void registerRecordDefn(RecordBuffer rBuf);
  public abstract void registerChildScrollDefn(ScrollBuffer scrollBuf);
  public abstract int getIndexPositionOfRecord(PTRecord record);
  public abstract int getIndexOfThisRowInParentRowset();

  /**
   * Retrieve the record associated with the record name provided
   * @return the record associated with the record name provided
   */
  public PTRecord getRecord(final String recName) {
    return this.recordMap.get(recName);
  }

  /**
   * Retrieves the record at the given index (records are stored
   * in a linked hash map and thus are ordered).
   */
  public PTRecord getRecord(final int index) {
    // Remember: PS uses 1-based indices, not 0-based, must adjust here.
    return (PTRecord) this.recordMap.values().toArray()[index - 1];
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
    final List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if (args.size() != 1) {
      throw new OPSVMachRuntimeException("Expected only one arg.");
    }

    PTRecord rec = null;
    if(args.get(0) instanceof PTRecordLiteral) {
      rec = this.getRecord(((PTRecordLiteral) args.get(0)).read());
    } else if (args.get(0) instanceof PTInteger) {
      rec = this.getRecord(((PTInteger) args.get(0)).read());
    } else {
      throw new OPSVMachRuntimeException("Expected arg to GetRecord() to "
          + "be a PTRecordLiteral or PTInteger.");
    }

    Environment.pushToCallStack(rec);
  }

  @Override
  public PTType dotProperty(final String s) {
    if (this.recordMap.containsKey(s)) {
      return this.recordMap.get(s);
    } else if (s.toLowerCase().equals("recordcount")) {
      return new PTInteger(this.registeredRecordDefns.size());
    } else if (s.toLowerCase().equals("parentrowset")) {
      return this.parentRowset;
    } else if (s.toLowerCase().equals("selected")) {
      return this.selectedPropertyRef;
    }
    return null;
  }

  public PTRowset getParentRowset() {
    return this.parentRowset;
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
}
