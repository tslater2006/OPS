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
 * Represents a PeopleTools standalone record (not in the component buffer).
 */
public final class PTStandaloneRecord extends PTRecord implements ICBufferEntity {

  private static Logger log = LogManager.getLogger(PTStandaloneRecord.class.getName());

  private static Map<String, Method> ptMethodTable;

  private PTRow parentRow;
  private Record recDefn;
  private RecordBuffer recBuffer;

  static {
    final String PT_METHOD_PREFIX = "PT_";

    // cache pointers to PeopleTools Record methods.
    final Method[] methods = PTStandaloneRecord.class.getMethods();
    ptMethodTable = new HashMap<String, Method>();
    for (Method m : methods) {
      if (m.getName().indexOf(PT_METHOD_PREFIX) == 0) {
        ptMethodTable.put(m.getName().substring(
            PT_METHOD_PREFIX.length()), m);
      }
    }
  }

  public PTStandaloneRecord(final PTRecordTypeConstraint origTc,
      final PTRow pRow, final Record r) {
    super(origTc);
    this.parentRow = pRow;
    this.recDefn = r;
    this.init();
  }

  public PTStandaloneRecord(final PTRecordTypeConstraint origTc,
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
          throw new OPSVMachRuntimeException("MQUINN 11-30-2014 : Disabling for split.");
/*          newFldRef
            = new PTImmutableReference<PTField>(fldTc,
                fldTc.allocBufferField(this, this.recBuffer.getRecordFieldBuffer(rf.FIELDNAME)));*/
        } else {
          newFldRef
            = new PTImmutableReference<PTField>(fldTc, fldTc.allocStandaloneField(this, rf));
        }
        this.fieldRefs.put(rf.FIELDNAME, newFldRef);
        this.fieldRefIdxTable.put(i++, newFldRef);
      } catch (final OPSTypeCheckException opstce) {
        throw new OPSVMachRuntimeException(opstce.getMessage(), opstce);
      }
    }
  }

  /**
   * MQUINN 12-03-2014 : Remove after split.
   */
  public int getIndexPositionOfThisRecordInParentRow() {
    return -5;
  }
  public int getIndexPositionOfField(final PTField fld) {
    return -5;
  }
  public void emitRecInScroll() {
  }
  public void emitScrolls(final String indent) {
  }
  public void fireEvent(final PCEvent event,
      final FireEventSummary fireEventSummary) {
  }
  public void runFieldDefaultProcessing(
      final FieldDefaultProcSummary fldDefProcSummary) {
  }
  public PTRecord resolveContextualCBufferRecordReference(final String recName) {
    return null;
  }
  public PTReference<PTField> resolveContextualCBufferRecordFieldReference(
      final String recName, final String fieldName) {
    return null;
  }
  public PTRowset resolveContextualCBufferScrollReference(
      final PTScrollLiteral scrollName) {
    return null;
  }
  public void generateKeylist(
      final String fieldName, final Keylist keylist) {
  }

  public PTRow getParentRow() {
    return this.parentRow;
  }

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
