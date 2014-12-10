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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
public abstract class PTRecord<R extends PTRow, F extends PTField>
    extends PTObjectType {

  private static Logger log = LogManager.getLogger(PTRecord.class.getName());

  private static Map<String, Method> ptMethodTable;
  private static Pattern dtPattern, datePattern, dotPattern;

  protected Record recDefn;
  protected R parentRow;
  protected Map<String, PTImmutableReference<F>> fieldRefs;
  protected Map<Integer, PTImmutableReference<F>> fieldRefIdxTable;

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

  public R getParentRow() {
    return this.parentRow;
  }

  public Record getRecDefn() {
    return this.recDefn;
  }

  public List<PTImmutableReference<F>> getFieldRefsInAlphabeticOrderByFieldName() {
    final Map<String, PTImmutableReference<F>> orderedRefMap =
        new TreeMap<>(this.fieldRefs);
    return new ArrayList<PTImmutableReference<F>>(orderedRefMap.values());
  }

  /**
   * Retrieves the field object corresponding to the
   * provided field name.
   * @param fldName the name of the field to retrieve
   * @return the field object corresponding to the provided name
   */
  public PTImmutableReference<F> getFieldRef(final String fldName) {

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

  @Override
  public PTType dotProperty(final String s) {
    if (this.fieldRefs.containsKey(s)) {
      return this.fieldRefs.get(s);
    } else if (s.toLowerCase().equals("name")) {
      return new PTString(this.recDefn.RECNAME);
    } else if (s.toLowerCase().equals("fieldcount")) {
      return new PTInteger(this.fieldRefs.size());
    } else if (s.toLowerCase().equals("parentrow")) {
      return this.parentRow;
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

  /**
   * Determines whether this record has the provided
   * field within it.
   * @param fldName the name of the field to check
   * @return true if the field exists, false otherwise
   */
  public boolean hasField(final String fldName) {
    return this.fieldRefs.containsKey(fldName);
  }

  /**
   * Recursively sets the default value for every field object within
   * this record object.
   */
  public void setDefault() {
    for (Map.Entry<String, PTImmutableReference<F>> cursor
        : this.fieldRefs.entrySet()) {
      cursor.getValue().deref().setBlank();
    }
  }

  public void PT_GetField() {

    final List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if (args.size() != 1) {
      throw new OPSVMachRuntimeException("Expected a single arg to GetField.");
    }

    final PTType arg = args.get(0);
    final F fld;
    if (arg instanceof PTInteger) {
      // We do not need to adjust the provided index; fieldRefIdxTable maps
      // fields to their PT indices (meaning 1-based instead of 0-based).
      final int fldIdx = ((PTInteger) arg).read();
      fld = this.fieldRefIdxTable.get(fldIdx).deref();
    } else if (arg instanceof PTFieldLiteral) {
      final String fldName = ((PTFieldLiteral) arg).getFieldName();
      fld = this.fieldRefs.get(fldName).deref();
    } else {
      throw new OPSVMachRuntimeException("Expected arg to GetField to be either "
          + "an integer or a field literal, is actually: " + arg.getClass().getName());
    }

    Environment.pushToCallStack(fld);
  }

  /**
   * Implements the .SetDefault PeopleCode method for record objects.
   * Recursively sets the default value for every field object within this
   * record object. Arguments must be placed on the OPS runtime stack.
   */
  public void PT_SetDefault() {
    final List<PTType> args = Environment.getArgsFromCallStack();
    if (args.size() != 0) {
      throw new OPSVMachRuntimeException("Expected no args.");
    }
    this.setDefault();
  }

  /**
   * Implements the .SelectByKeyEffDt PeopleCode method for record objects.
   * Arguments must be placed on the OPS runtime stack.
   */
  public void PT_SelectByKeyEffDt() {
    final List<PTType> args = Environment.getArgsFromCallStack();
    if (args.size() != 1 || (!(args.get(0) instanceof PTDate))) {
      throw new OPSVMachRuntimeException("Expected single date arg.");
    }

    final OPSStmt ostmt = StmtLibrary.prepareSelectByKeyEffDtStmt(
        this.recDefn, this, (PTDate) args.get(0));
    final OPSResultSet rs = ostmt.executeQuery();

    final List<RecordField> rfList = this.recDefn.getExpandedFieldList();
    final int numCols = rs.getColumnCount();
    if (numCols != rfList.size()) {
      throw new OPSVMachRuntimeException("The number of columns returned "
          + "by the select by key query (" + numCols
          + ") differs from the number "
          + "of fields (" + rfList.size()
          + ") in the record defn field list.");
    }

    /*
     * Although multiple rows may exist in the ResultSet,
     * only one row is read by SelectByKeyEffDt.
     */
    PTBoolean returnVal = new PTBoolean(false);
    if (rs.next()) {
      rs.readIntoRecord(this);
      returnVal = new PTBoolean(true);
    }

    rs.close();
    ostmt.close();

    // Return true if record was read, false otherwise.
    Environment.pushToCallStack(returnVal);
  }

  /**
   * Implements the .SelectByKey PeopleTools method for record objects.
   * Arguments must be placed on the OPS runtime stack.
   */
  public void PT_SelectByKey() {
    final List<PTType> args = Environment.getArgsFromCallStack();
    if (args.size() != 0) {
      throw new OPSVMachRuntimeException("Expected zero args.");
    }

    final OPSStmt ostmt = StmtLibrary.prepareSelectByKey(this.recDefn, this);
    final OPSResultSet rs = ostmt.executeQuery();

    final List<RecordField> rfList = this.recDefn.getExpandedFieldList();
    final int numCols = rs.getColumnCount();

    if (numCols != rfList.size()) {
      throw new OPSVMachRuntimeException("The number of columns returned "
          + "by the select by key query (" + numCols + ") differs "
          + "from the number "
          + "of fields (" + rfList.size()
          + ") in the record defn field list.");
    }

    /*
     * VERY IMPORTANT!
     * The PT documentation is somewhat vague and leaves room open to
     * interpretation for some things (i.e., if multiple records are returned,
     * is that an error?). Using the PS debugger, I found the answer to that
     * question to be no; PT seems to stop reading after the first record, and
     * the return value for SelectByKey is True. Just keep in mind that before
     * modifying this code and the rest of this function, compare the official
     * documentation with an actual debugging session first.
     */
    PTBoolean returnVal = new PTBoolean(false);
    while (rs.next()) {
      // is this the first row in rs?
      if (rs.isFirst()) {
        rs.readIntoRecord(this);
        returnVal = new PTBoolean(true);
        break;
      }
    }

    rs.close();
    ostmt.close();

    Environment.pushToCallStack(returnVal);
  }

  /**
   * Overrides parent method because calls to make a record
   * read-only should make its fields read-only as well.
   */
  @Override
  public void setReadOnly() {
    super.setReadOnly();
    if (this.fieldRefs != null) {
      for (Map.Entry<String, PTImmutableReference<F>> cursor
          : this.fieldRefs.entrySet()) {
        cursor.getValue().deref().setReadOnly();
      }
    }
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(":").append(this.recDefn.RECNAME);
    b.append(",fieldRefs=").append(this.fieldRefs);
    return b.toString();
  }
}
