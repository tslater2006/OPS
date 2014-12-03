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
  private static Pattern dtPattern, datePattern, dotPattern;

  protected Map<String, PTImmutableReference<PTField>> fieldRefs;
  protected Map<Integer, PTImmutableReference<PTField>> fieldRefIdxTable;

  static {
    /*
     * "ASTIMESTAMP" is not a typo; no spaces exist in the name as
     * returned in the ResultSet.
     */
    dtPattern = Pattern.compile(
        "TO_CHAR\\(CAST\\(\\(([^\\)]*)\\)ASTIMESTAMP\\),"
        + "'YYYY-MM-DD-HH24\\.MI\\.SS\\.FF'\\)");

    datePattern = Pattern.compile(
        "TO_CHAR\\(([^,]*),'YYYY-MM-DD'\\)");

    dotPattern = Pattern.compile("([^\\.]*)\\.(.*)");

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
  public abstract void emitScrolls(String indent);
  public abstract void fireEvent(PCEvent event, FireEventSummary fireEventSummary);
  public abstract void runFieldDefaultProcessing(FieldDefaultProcSummary fldDefProcSummary);
  public abstract PTRowset resolveContextualCBufferScrollReference(PTScrollLiteral scrollName);
  public abstract PTRecord resolveContextualCBufferRecordReference(String recName);
  public abstract PTReference<PTField> resolveContextualCBufferRecordFieldReference(String recName, String fldName);
  public abstract int determineScrollLevel();
  public abstract Record getRecDefn();
  public abstract void setDefault();
  public abstract int getIndexPositionOfField(PTField field);
  public abstract RecordBuffer getRecBuffer();
  public abstract PTRow getParentRow();
  public abstract void emitRecInScroll();
  public abstract boolean hasField(String fieldName);
  public abstract void firstPassFill();
  public abstract int getIndexPositionOfThisRecordInParentRow();

  public Map<String, PTImmutableReference<PTField>> getFieldRefs() {
    return this.fieldRefs;
  }

  /**
   * Retrieves the field object corresponding to the
   * provided field name.
   * @param fldName the name of the field to retrieve
   * @return the field object corresponding to the provided name
   */
  public PTImmutableReference<PTField> getFieldRef(final String fldName) {

    String unwrappedFldName = fldName;

    /*
     * Unwrap datetime fields.
     */
    final Matcher dtMatcher = dtPattern.matcher(unwrappedFldName);
    if (dtMatcher.find()) {
      unwrappedFldName = dtMatcher.group(1);
    }

    /*
     * Unwrap date fields.
     */
    final Matcher dateMatcher = datePattern.matcher(unwrappedFldName);
    if (dateMatcher.find()) {
      unwrappedFldName = dateMatcher.group(1);
    }

    /*
     * Remove any record or "FILL" prefix from the field name.
     */
    if (unwrappedFldName.contains(".")) {
      final Matcher dotMatcher = dotPattern.matcher(unwrappedFldName);
      if (dotMatcher.find()) {
        unwrappedFldName = dotMatcher.group(2);
      }
    }

    if (!this.fieldRefs.containsKey(unwrappedFldName)) {
      throw new OPSVMachRuntimeException("Call to getFieldRef with "
          + "unwrappedFldName=" + unwrappedFldName
          + " did not match any field on this record: "
          + this.toString());
    }
    return this.fieldRefs.get(unwrappedFldName);
  }
}
