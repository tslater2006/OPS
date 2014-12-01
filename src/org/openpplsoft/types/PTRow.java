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
  public abstract PTRecord getRecord(String recName);
  public abstract PTRowset getRowset(String primaryRecName);
  public abstract PTRecord getRecord(int index);
  public abstract void registerRecordDefn(Record rec);
  public abstract Map<String, PTRecord> getRecordMap();
  public abstract void registerRecordDefn(RecordBuffer rBuf);
  public abstract void registerChildScrollDefn(ScrollBuffer scrollBuf);
  public abstract int getIndexPositionOfRecord(PTRecord record);
  public abstract PTRowset getParentRowset();
  public abstract int getIndexOfThisRowInParentRowset();
}
