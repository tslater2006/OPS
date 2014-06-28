/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.lang.reflect.Method;

import java.sql.ResultSet;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.sql.*;

/**
 * Represents a PeopleTools record object.
 */
public final class PTRecord extends PTObjectType {

  private static Logger log = LogManager.getLogger(PTRecord.class.getName());

  private static Type staticTypeFlag;
  private static Map<String, Method> ptMethodTable;
  private static Pattern dtPattern, datePattern, dotPattern;

  private Record recDefn;
  private Map<String, PTField> fields;
  private Map<Integer, PTField> fieldIdxTable;

  static {
    staticTypeFlag = Type.RECORD;

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

  /**
   * Creates a new record object that isn't attached
   * to a record defn; can only be called by local methods.
   */
  protected PTRecord() {
    super(staticTypeFlag);
  }

  /**
   * Creates a new record object that is attached
   * to a record defn; can only be called by local methods.
   * @param r the record defn to attach
   */
  protected PTRecord(final Record r) {
    super(staticTypeFlag);

    this.recDefn = r;

    // this map is linked in order to preserve
    // the order in which fields are added.
    this.fields = new LinkedHashMap<String, PTField>();
    this.fieldIdxTable = new LinkedHashMap<Integer, PTField>();
    int i = 1;
    for (RecordField rf : this.recDefn.getExpandedFieldList()) {
      final PTField newFld = PTField.getSentinel().alloc(rf);
      this.fields.put(rf.FIELDNAME, newFld);
      this.fieldIdxTable.put(i++, newFld);
    }
  }

  /**
   * Retrieves the underlying record defn.
   * @return the underlying record defn
   */
  public Record getRecDefn() {
    return this.recDefn;
  }

  /**
   * Retrieves the fields within this record.
   * @return a map of the field names to the fields
   *   themselves
   */
  public Map<String, PTField> getFields() {
    return this.fields;
  }

  @Override
  public PTType dotProperty(final String s) {
    return this.fields.get(s);
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
    return this.fields.containsKey(fldName);
  }

  /**
   * Retrieves the field object corresponding to the
   * provided field name.
   * @param fldName the name of the field to retrieve
   * @return the field object corresponding to the provided name
   */
  public PTField getField(final String fldName) {

    String unwrappedFldName = fldName;

    /*
     * Unwrap datetime fields.
     * "ASTIMESTAMP" is not a typo; no spaces exist in the name as
     * returned in the ResultSet.
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

    if (!this.fields.containsKey(unwrappedFldName)) {
      throw new OPSVMachRuntimeException("Call to getField with "
          + "unwrappedFldName=" + unwrappedFldName
          + " did not match any field on this record: "
          + this.toString());
    }
    return this.fields.get(unwrappedFldName);
  }

  /**
   * Recursively sets the default value for every field object within
   * this record object.
   */
  private void setDefault() {
    for (Map.Entry<String, PTField> cursor : this.fields.entrySet()) {
      cursor.getValue().setDefault();
    }
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
    ResultSet rs = null;

    try {
      final List<RecordField> rfList = this.recDefn.getExpandedFieldList();
      rs = ostmt.executeQuery();

      final int numCols = rs.getMetaData().getColumnCount();
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
      PTBoolean returnVal = Environment.FALSE;
      if (rs.next()) {
        GlobalFnLibrary.readRecordFromResultSet(
          this.recDefn, this, rs);
        returnVal = Environment.TRUE;
      }

      // Return true if record was read, false otherwise.
      Environment.pushToCallStack(returnVal);

    } catch (final java.sql.SQLException sqle) {
      log.fatal(sqle.getMessage(), sqle);
      System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
    } finally {
      try {
        if (rs != null) { rs.close(); }
        if (ostmt != null) { ostmt.close(); }
      } catch (final java.sql.SQLException sqle) {
        log.warn("Unable to close rs and/or ostmt in finally block.");
      }
    }
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
    ResultSet rs = null;

    try {
      final List<RecordField> rfList = this.recDefn.getExpandedFieldList();
      rs = ostmt.executeQuery();

      final int numCols = rs.getMetaData().getColumnCount();
      if (numCols != rfList.size()) {
        throw new OPSVMachRuntimeException("The number of columns returned "
            + "by the select by key query (" + numCols + ") differs "
            + "from the number "
            + "of fields (" + rfList.size()
            + ") in the record defn field list.");
      }

      /*
       * True should be returned only if exactly one row
       * was read.
       */
      PTBoolean returnVal = Environment.FALSE;
      while (rs.next()) {
        // is this the first row in rs?
        if (rs.isFirst()) {
          GlobalFnLibrary.readRecordFromResultSet(
              this.recDefn, this, rs);
          returnVal = Environment.TRUE;
        } else {
          returnVal = Environment.FALSE;
          throw new OPSVMachRuntimeException("Multiple rows "
            + "returned by SelectByKey; although this is "
            + "not an error, review the documentation to ensure "
            + "necessary actions to set default values are being "
            + "taken.");
          //break;
        }
      }

      // Return true if record was read, false if
      // 0 or multiple recs read.
      Environment.pushToCallStack(returnVal);

    } catch (final java.sql.SQLException sqle) {
      log.fatal(sqle.getMessage(), sqle);
      System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
    } finally {
      try {
        if (rs != null) { rs.close(); }
        if (ostmt != null) { ostmt.close(); }
      } catch (final java.sql.SQLException sqle) {
        log.warn("Unable to close rs and/or ostmt in finally block.");
      }
    }
  }

  /**
   * Overrides parent method because calls to make a record
   * read-only should make its fields read-only as well.
   * @return this record object (enables method chaining)
   */
  @Override
  public PTType setReadOnly() {
    super.setReadOnly();
    if (this.fields != null) {
      for (Map.Entry<String, PTField> cursor : this.fields.entrySet()) {
        cursor.getValue().setReadOnly();
      }
    }
    return this;
  }

  @Override
  public PTPrimitiveType castTo(final PTPrimitiveType t) {
    throw new EntDataTypeException("castTo() has not been implemented.");
  }

  @Override
  public boolean typeCheck(final PTType a) {
    return (a instanceof PTRecord
        && this.getType() == a.getType());
  }

  /**
   * Retrieves a sentinel record object from the cache, or creates
   * it if one does not exist.
   * @return the sentinel record object
   */
  public static PTRecord getSentinel() {

    // If the sentinel has already been cached, return it immediately.
    final String cacheKey = getCacheKey();
    if (PTType.isSentinelCached(cacheKey)) {
      return (PTRecord) PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    final PTRecord sentinelObj = new PTRecord();
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  /**
   * Allocates a new record object with an attached record defn.
   * Allocated records must have an associated record defn in order
   * to determine the type of the value enclosed within them. However, this
   * defn is not part of the type itself; a Record variable can be assigned
   * any Record object, regardless of its underlying record defn.
   * @param r the record defn to attach
   * @return the newly allocated record object
   */
  public PTRecord alloc(final Record r) {
    final PTRecord newObj = new PTRecord(r);
    PTType.clone(this, newObj);
    return newObj;
  }

  private static String getCacheKey() {
    return new StringBuilder(staticTypeFlag.name()).toString();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(":").append(this.recDefn.RECNAME);
    b.append(",fields=").append(this.fields);
    return b.toString();
  }
}
