/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.PeopleToolsImplementation;
import org.openpplsoft.runtime.Environment;
import org.openpplsoft.runtime.OPSVMachRuntimeException;

/**
 * Represents a PeopleTools row definition; contains
 * 1 to n child records and 0 to m child rowsets.
 */
public abstract class PTRow<R extends PTRowset, E extends PTRecord>
    extends PTObjectType {

  private static final Logger log = LogManager.getLogger(PTRow.class.getName());

  private boolean isNewRow;

  // Maps record names to child record objects
  protected final Map<String, E> recordMap;

  // Maps scroll/rowset primary rec names to rowset objects
  protected final Map<String, R> rowsetMap;

  protected R parentRowset;
  protected PTImmutableReference<PTBoolean> selectedPropertyRef;

  public PTRow(final PTRowTypeConstraint origTc) {
    super(origTc);
    this.isNewRow = true;
    this.recordMap = new LinkedHashMap<>();
    this.rowsetMap = new LinkedHashMap<>();

    try {
      /*
       * Initialize read/write properties.
       */
      this.selectedPropertyRef
          = new PTImmutableReference<PTBoolean>(
              PTBoolean.getTc(), new PTBoolean(false));
    } catch (final OPSTypeCheckException opstce) {
      throw new OPSVMachRuntimeException(opstce.getMessage(), opstce);
    }
  }

  public boolean isNew() {
    return this.isNewRow;
  }

  protected void untagAsNew() {
    this.isNewRow = false;
  }

  public R getParentRowset() {
    return this.parentRowset;
  }

  /**
   * Retrieve the record associated with the record name provided
   * @return the record associated with the record name provided
   */
  public E getRecord(final String recName) {
    return this.recordMap.get(recName);
  }

  /**
   * Retrieves the record at the given index (records are stored
   * in a linked hash map and thus are ordered).
   */
  public E getRecord(final int index) {
    final List<E> orderedList = new ArrayList<E>(this.recordMap.values());

    // Remember: PS uses 1-based indices, not 0-based, must adjust here.
    return orderedList.get(index - 1);
  }

  public R getRowset(final String primaryRecName) {
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
  @PeopleToolsImplementation
  public void GetRecord() {
    final List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if (args.size() != 1) {
      throw new OPSVMachRuntimeException("Expected only one arg.");
    }

    E rec = null;
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
      return new PTInteger(this.recordMap.size());
    } else if (s.toLowerCase().equals("parentrowset")) {
      return this.parentRowset;
    } else if (s.toLowerCase().equals("selected")) {
      return this.selectedPropertyRef;
    }
    return null;
  }

  public Map<String, E> getRecordMap() {
    return this.recordMap;
  }

  @Override
  public void setReadOnly() {
    super.setReadOnly();

    // Calls to make a row read-only must make its child records read-only.
    for(Map.Entry<String, E> cursor: this.recordMap.entrySet()) {
      cursor.getValue().setReadOnly();
    }

    // Calls to make a row read-only must make its child rowsets read-only.
    for(Map.Entry<String, R> cursor: this.rowsetMap.entrySet()) {
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
