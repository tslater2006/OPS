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
import org.openpplsoft.runtime.*;
import org.openpplsoft.trace.*;
import org.openpplsoft.sql.*;
import org.openpplsoft.pt.peoplecode.*;
import org.openpplsoft.buffers.*;

/**
 * Represents a PeopleTools record object.
 */
public final class PTRecord extends PTObjectType implements ICBufferEntity {

  private static Logger log = LogManager.getLogger(PTRecord.class.getName());

  private static Map<String, Method> ptMethodTable;
  private static Pattern dtPattern, datePattern, dotPattern;

  private PTRow parentRow;
  private Record recDefn;
  private RecordBuffer recBuffer;
  private Map<String, PTImmutableReference<PTField>> fieldRefs;
  private Map<Integer, PTImmutableReference<PTField>> fieldRefIdxTable;

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

  public PTRecord(final PTRecordTypeConstraint origTc,
      final PTRow pRow, final Record r) {
    super(origTc);
    this.parentRow = pRow;
    this.recDefn = r;
    this.init();
  }

  public PTRecord(final PTRecordTypeConstraint origTc,
      final PTRow pRow, final RecordBuffer recBuffer) {
    super(origTc);
    this.parentRow = pRow;
    this.recDefn = recBuffer.getRecDefn();
    this.recBuffer = recBuffer;
    this.init();
  }

  private void init() {
    // this map is linked in order to preserve
    // the order in which fields are added.
    this.fieldRefs = new LinkedHashMap<String, PTImmutableReference<PTField>>();
    this.fieldRefIdxTable =
        new LinkedHashMap<Integer, PTImmutableReference<PTField>>();
    int i = 1;
    for (final RecordField rf : this.recDefn.getExpandedFieldList()) {
      PTFieldTypeConstraint fldTc = new PTFieldTypeConstraint();

      try {
        PTImmutableReference<PTField> newFldRef = null;

        // If this record field has a buffer associated with it, allocate the
        // field with that to give the field a reference to that buffer.
        if (this.recBuffer != null
            && this.recBuffer.hasRecordFieldBuffer(rf.FIELDNAME)) {
          newFldRef
            = new PTImmutableReference<PTField>(fldTc,
                fldTc.alloc(this, this.recBuffer.getRecordFieldBuffer(rf.FIELDNAME)));
        } else {
          newFldRef
            = new PTImmutableReference<PTField>(fldTc, fldTc.alloc(this, rf));
        }
        this.fieldRefs.put(rf.FIELDNAME, newFldRef);
        this.fieldRefIdxTable.put(i++, newFldRef);
      } catch (final OPSTypeCheckException opstce) {
        throw new OPSVMachRuntimeException(opstce.getMessage(), opstce);
      }
    }
  }

  public void fireEvent(final PCEvent event,
      final FireEventSummary fireEventSummary) {

    // Fire event on each field in this record.
    for (Map.Entry<String, PTImmutableReference<PTField>> entry
        : this.fieldRefs.entrySet()) {
      entry.getValue().deref().fireEvent(event, fireEventSummary);
    }

    // PeopleCode events fire only on entities in the component buffer.
    // (make an exception for SearchInit, as search records do not have a buffer
    // in component  technically).
    if (this.recBuffer == null && event != PCEvent.SEARCH_INIT) {
      return;
    }

    /*
     * If a Component PeopleCode program has been written for this event, run it now.
     * Note that there is no way to define a Record PeopleCode program on a record
     * (must be attached to a Record Field) so we only check for Component PeopleCode here.
     */
    final PeopleCodeProg compProg = ComponentBuffer.getComponentDefn()
        .getProgramForRecordEvent(event, this.recDefn);
    if (compProg != null && compProg.hasAtLeastOneStatement()) {
      final ExecContext eCtx = new ProgramExecContext(compProg,
          this.determineScrollLevel(), this.determineRowIndex());
      // Pass this record to the supervisor as the component buffer context.
      final InterpretSupervisor interpreter = new InterpretSupervisor(eCtx, this);
      interpreter.run();
      fireEventSummary.incrementNumEventProgsExecuted();
    }
  }

  public void runFieldDefaultProcessing(
      final FieldDefaultProcSummary fldDefProcSummary) {

    // Run non-constant (from record) field default processing
    // on each field in this record.
    for (Map.Entry<String, PTImmutableReference<PTField>> entry
        : this.fieldRefs.entrySet()) {
      // Note: callee will exit if field is not blank per fld def proc logic in PS.
      entry.getValue().deref().runNonConstantFieldDefaultProcessing(fldDefProcSummary);
      entry.getValue().deref().runConstantFieldDefaultProcessing(fldDefProcSummary);
    }

    // Run constant field default processing
    // on each field in this record.
    for (Map.Entry<String, PTImmutableReference<PTField>> entry
        : this.fieldRefs.entrySet()) {
      // Note: callee will exit if field is not blank per fld def proc logic in PS.
    }

    // For any fields that are still blank, fire the FieldDefault event.
    // NOTE: This cannot be done in the PTField call to runFieldDefaultProcessing,
    // because FieldDefault events only fire once every field has been assigned
    // a constant/record default (if defined).
    for (Map.Entry<String, PTImmutableReference<PTField>> entry
        : this.fieldRefs.entrySet()) {
      final PTField fld = entry.getValue().deref();
      final PTPrimitiveType fldValue = fld.getValue();
      final RecordField recFieldDefn = fld.getRecordFieldDefn();

      // Only fire event if field is blank and has a buffer in the component.
      if (fldValue.isBlank() && fld.getRecordFieldBuffer() != null) {
        boolean preFieldDefaultFireIsMarkedAsUpdated = fldValue.isMarkedAsUpdated();

        final FireEventSummary summary = new FireEventSummary();
        entry.getValue().deref().fireEvent(PCEvent.FIELD_DEFAULT, summary);

        if (summary.getNumEventProgsExecuted() > 0) {
          final PCFldDefaultEmission fdEmission = new PCFldDefaultEmission(
              recFieldDefn.RECNAME, recFieldDefn.FIELDNAME);
          fdEmission.setDefaultedValue("from peoplecode");

          /*
           * At this point, a FieldDefault program may have been run,
           * but it may not have actually changed the field's values. If
           * If it did, an emission must be made indicating as much.
           */
          if (!preFieldDefaultFireIsMarkedAsUpdated && fldValue.isMarkedAsUpdated()) {
            fldDefProcSummary.fieldWasChanged();
            TraceFileVerifier.submitEnforcedEmission(fdEmission);
          } else if (fldValue.isBlank()) {
            fldDefProcSummary.blankFieldWasSeen();
          }
        }
      }
    }
  }

  public PTType resolveContextualCBufferReference(final String identifier) {
    if (identifier.equals(this.recDefn.RECNAME)) {
      return this;
    } else if (this.parentRow != null) {
      return this.parentRow.resolveContextualCBufferReference(identifier);
    }
    return null;
  }

  /**
   * Key lookups are always passed up when reaching a PTRecord;
   * call does not go to the record's parent row, but to the record's parent row's
   * parent row (since key lookup does not involve searching records in the same
   * row as this record, but the ones that exist on the row above it). Thus, if
   * this record
   * is in scroll level 2, the records in both scroll level 1 and scroll level 0
   * will be searched within the two rows that make up the buffer context hierarchy
   * leading to the row that this record is in.
   */
  public void generateKeylist(
      final String fieldName, final Keylist keylist) {

    if (this.parentRow == null
        || this.parentRow.getParentRowset() == null
        || this.parentRow.getParentRowset().getParentRow() == null) {
      return;
    }

    this.parentRow.getParentRowset().getParentRow()
          .generateKeylist(fieldName, keylist);
  }

  public PTRow getParentRow() {
    return this.parentRow;
  }

  /**
   * Retrieves the underlying record defn.
   * @return the underlying record defn
   */
  public Record getRecDefn() {
    return this.recDefn;
  }

  public RecordBuffer getRecBuffer() {
    return this.recBuffer;
  }

  public int determineRowIndex() {
    if (this.parentRow != null) {
      return this.parentRow.getIndexOfThisRowInParentRowset();
    }

    throw new OPSVMachRuntimeException("Failed to determine ancestral row "
        + "index; parent row is null.");
  }

  public int determineScrollLevel() {
    if (this.parentRow != null) {
      return this.parentRow.determineScrollLevel();
    }

    throw new OPSVMachRuntimeException("Failed to determine scroll level of "
        + "this record; parent row is null.");
  }

  /**
   * Retrieves the field references within this record.
   * @return a map of the field names to the immutable references
   *    to those fields.
   */
  public Map<String, PTImmutableReference<PTField>> getFieldRefs() {
    return this.fieldRefs;
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

  /**
   * Recursively sets the default value for every field object within
   * this record object.
   */
  public void setDefault() {
    for (Map.Entry<String, PTImmutableReference<PTField>> cursor
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
    final PTField fld;
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
   * TODO: This likely needs to be renamed. It is meant for use
   * when filling records in scroll level 0, but the logic here
   * will likely need to be genericized to other scrolls once
   * I get to that point.
   */
  public void firstPassFill() {

    OPSStmt ostmt = null;

    final Record recDefn = DefnCache.getRecord(this.recDefn.RECNAME);
    if (recDefn.hasARequiredKeyField() || recDefn.hasNoKeys()) {
      ostmt = StmtLibrary.prepareFirstPassFillQuery(this);
    } else {
      return;
    }

    /*
     * If null comes back, one or more key values is not
     * available, and thus the fill cannot be run.
     */
    if (ostmt == null) { return; }

    final OPSResultSet rs = ostmt.executeQuery();
    final int numCols = rs.getColumnCount();

    // NOTE: record may legitimately be empty.
    if (rs.next()) {
      rs.readIntoRecord(this);
    }

    rs.close();
    ostmt.close();
  }

  /**
   * Overrides parent method because calls to make a record
   * read-only should make its fields read-only as well.
   */
  @Override
  public void setReadOnly() {
    super.setReadOnly();
    if (this.fieldRefs != null) {
      for (Map.Entry<String, PTImmutableReference<PTField>> cursor
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
