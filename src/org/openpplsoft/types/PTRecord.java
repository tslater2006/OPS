/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.lang.reflect.Method;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.*;
import org.openpplsoft.pt.pages.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.trace.*;
import org.openpplsoft.sql.*;
import org.openpplsoft.pt.peoplecode.*;
import org.openpplsoft.buffers.*;

/**
 * Represents a PeopleTools record object.
 */
public abstract class PTRecord extends PTObjectType {

  private static Logger log = LogManager.getLogger(PTRecord.class.getName());

  private static Map<String, Method> ptMethodTable;

  static {
    final String PT_METHOD_PREFIX = "PT_";

    // cache pointers to PeopleTools Record methods.
    final Method[] methods = PTRecord.class.getMethods();
    ptMethodTable = new HashMap<String, Method>();
    for (Method m : methods) {
      if (m.getName().indexOf(PT_METHOD_PREFIX) == 0) {
        ptMethodTable.put(m.getName().substring(
            PT_METHOD_PREFIX.length()), m);
      }
    }
  }

  public PTRecord(final PTRecordTypeConstraint origTc) {
    super(origTc);
  }

  public abstract void generateKeylist(String fieldName, Keylist keylist);
  public abstract boolean isInComponentBuffer();
  public abstract void emitScrolls(String indent);
  public abstract void fireEvent(PCEvent event, FireEventSummary fireEventSummary);
  public abstract void runFieldDefaultProcessing(FieldDefaultProcSummary fldDefProcSummary);
  public abstract PTRowset resolveContextualCBufferScrollReference(PTScrollLiteral scrollName);
  public abstract PTRecord resolveContextualCBufferRecordReference(String recName);
  public abstract PTReference<PTField> resolveContextualCBufferRecordFieldReference(String recName, String fldName);
  public abstract int determineScrollLevel();
  public abstract Record getRecDefn();
  public abstract void setDefault();
  public abstract Map<String, PTImmutableReference<PTField>> getFieldRefs();
  public abstract int getIndexPositionOfField(PTField field);
  public abstract RecordBuffer getRecBuffer();
  public abstract PTRow getParentRow();
  public abstract void emitRecInScroll();
  public abstract boolean hasField(String fieldName);
  public abstract PTImmutableReference<PTField> getFieldRef(String fieldName);
  public abstract void firstPassFill();
  public abstract int getIndexPositionOfThisRecordInParentRow();
}
