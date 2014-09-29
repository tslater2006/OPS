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

import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;

/**
 * Represents a PeopleTools row definition; contains
 * 1 to n child records and 0 to m child rowsets.
 */
public final class PTRow extends PTObjectType implements ICBufferEntity {

  private static Map<String, Method> ptMethodTable;

  // Maps record names to child record objects
  private Map<String, PTRecord> recordMap = new LinkedHashMap<String, PTRecord>();

  // Maps scroll/rowset primary rec names to rowset objects
  private Map<String, PTRowset> rowsetMap = new LinkedHashMap<String, PTRowset>();

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

  /**
   * Create a new row object that is attached to only one record
   * defn.
   * @param r the record defn to attach
   */
  public PTRow(final PTRowTypeConstraint origTc, final Record r) {
    super(origTc);
    this.recordMap.put(r.RECNAME, new PTRecordTypeConstraint().alloc(r));
  }

  /**
   * Create a new row object that is attached to one or more record defns.
   * @param s the set of record defns to attach
   */
  public PTRow(final PTRowTypeConstraint origTc, final Set<Record> s) {
    super(origTc);
    for(Record recDefn : s) {
      this.recordMap.put(recDefn.RECNAME,
          new PTRecordTypeConstraint().alloc(recDefn));
    }
  }

  public void fireEvent(final PCEvent event) {

    // Fire event on each record in this row.
    for (Map.Entry<String, PTRecord> entry : this.recordMap.entrySet()) {
      entry.getValue().fireEvent(event);
    }

    // Fire event on each rowset in this row.
    for (Map.Entry<String, PTRowset> entry : this.rowsetMap.entrySet()) {
      entry.getValue().fireEvent(event);
    }
  }

  public PTType resolveContextualCBufferReference(final String identifier) {
    throw new OPSVMachRuntimeException("TODO: Implement resolveContextualCBuffer... for Row.");
  }

  /**
   * Retrieve the record associated with the record name provided
   * @return the record associated with the record name provided
   */
  public PTRecord getRecord(final String recName) {
    return this.recordMap.get(recName);
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
