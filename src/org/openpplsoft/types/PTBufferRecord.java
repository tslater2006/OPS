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
 * Represents a PeopleTools record in the component buffer.
 */
public final class PTBufferRecord extends PTRecord<PTBufferRow, PTBufferField>
    implements ICBufferEntity {

  private static Logger log = LogManager.getLogger(PTBufferRecord.class.getName());

  private static Map<String, Method> ptMethodTable;

  private RecordBuffer recBuffer;

  static {
    final String PT_METHOD_PREFIX = "PT_";

    // cache pointers to PeopleTools Record methods.
    final Method[] methods = PTBufferRecord.class.getMethods();
    ptMethodTable = new HashMap<String, Method>();
    for (Method m : methods) {
      if (m.getName().indexOf(PT_METHOD_PREFIX) == 0) {
        ptMethodTable.put(m.getName().substring(
            PT_METHOD_PREFIX.length()), m);
      }
    }
  }

  public PTBufferRecord(final PTRecordTypeConstraint origTc,
      final PTBufferRow pRow, final RecordBuffer recBuffer) {
    super(origTc);
    this.parentRow = pRow;
    this.recDefn = recBuffer.getRecDefn();
    this.recBuffer = recBuffer;

    // this map is linked in order to preserve
    // the order in which fields are added.
    this.fieldRefs = new LinkedHashMap<String, PTImmutableReference<PTBufferField>>();
    this.fieldRefIdxTable =
        new LinkedHashMap<Integer, PTImmutableReference<PTBufferField>>();
    int i = 1;
    for (final RecordField rf : this.recDefn.getExpandedFieldList()) {
      PTFieldTypeConstraint fldTc = new PTFieldTypeConstraint();

      try {
        PTImmutableReference<PTBufferField> newFldRef = null;

        // If this record field has a buffer associated with it, allocate the
        // field with that to give the field a reference to that buffer. Otherwise,
        // allocate the field with just its record field defn.
        if (this.recBuffer.hasRecordFieldBuffer(rf.FIELDNAME)) {
          newFldRef = new PTImmutableReference<PTBufferField>(fldTc,
              fldTc.allocBufferField(this,
                  this.recBuffer.getRecordFieldBuffer(rf.FIELDNAME)));
        } else {
          newFldRef
              = new PTImmutableReference<PTBufferField>(fldTc,
                  fldTc.allocBufferField(this, rf));
        }
        this.fieldRefs.put(rf.FIELDNAME, newFldRef);
        this.fieldRefIdxTable.put(i++, newFldRef);
      } catch (final OPSTypeCheckException opstce) {
        throw new OPSVMachRuntimeException(opstce.getMessage(), opstce);
      }
    }
  }

  public int getIndexPositionOfThisRecordInParentRow() {
    return this.parentRow.getIndexPositionOfRecord(this);
  }

  public int getIndexPositionOfField(final PTBufferField fld) {
    int idxPos = 0;
    for (Map.Entry<Integer, PTImmutableReference<PTBufferField>> entry
        : fieldRefIdxTable.entrySet()) {
      if (entry.getValue().deref() == fld) {
        // We use this counter, rather than the hashmap key, because
        // the hashmap key is 1-based and this method should return the
        // 0-based index.
        return idxPos;
      }
      idxPos++;
    }

    throw new OPSVMachRuntimeException("The field provided does not exist "
        + "on this record; unable to get index position.");
  }

  public void emitRecInScroll() {

    if (PSDefn.isSystemRecord(this.recDefn.RECNAME)) {
      return;
    }

    int keyrec = -1, keyfield = -1;
    if (this.recBuffer.isRelatedDisplayRecBuffer()) {

      if (this.recBuffer.getFieldBuffers().size() > 1) {
        throw new OPSVMachRuntimeException("Expected only 1 field buffer "
            + "in related display rec buffer while emitting scrolls.");
      }

      final RecordFieldBuffer relDispFldBuf = this.recBuffer.getFieldBuffers().get(0);
      final PgToken relDispFldTok = relDispFldBuf.getOnlyPageFieldTok();

      /*
       * Remember: fields can serve as both display control AND
       * related display fields.
       */
      final PTBufferField dispCtrlFld =
          this.getParentRow().findDisplayControlField(relDispFldTok);

      keyrec = dispCtrlFld.getParentRecord()
          .getIndexPositionOfThisRecordInParentRow();
      keyfield = dispCtrlFld.getIndexPositionOfThisFieldInParentRecord();
    }
    TraceFileVerifier.submitEnforcedEmission(new RecInScroll(
        this.recDefn.RECNAME, keyrec, keyfield));
  }

  public void emitScrolls(final ScrollEmissionContext ctxFlag, final int indent) {

    if (PSDefn.isSystemRecord(this.recDefn.RECNAME)) {
      return;
    }

    // Do not log contents of a record that is not in the component buffer.
    if (this.recBuffer == null) {
      return;
    }

    String indentStr = "";
    for (int i = 0; i < indent; i++) {
      indentStr += "|  ";
    }

    String flagStr = "";
    if (ctxFlag != ScrollEmissionContext.SEARCH_RESULTS) {
      if (this.recDefn.isDerivedWorkRecord()) {
        flagStr += " work";
      } else {

        if (this.isEffectivelyAWorkRecord()) {
          log.debug("Effectively a work record: {}", this.recDefn.RECNAME);
          flagStr += " work";
        }

        // By defn, if record is not derived/work, it is physical and must
        // be marked as lvl0, even if it is also effectively a work record.
        flagStr += " lvl0";
      }
    }
    flagStr = flagStr.trim();

    TraceFileVerifier.submitEnforcedEmission(
        new CRecBuf(indentStr, this.recDefn.RECNAME,
            this.recDefn.getExpandedFieldList().size(), flagStr));

    for (Map.Entry<String, PTImmutableReference<PTBufferField>> entry
        : this.fieldRefs.entrySet()) {
      entry.getValue().deref().emitScrolls(ctxFlag, indent);
    }
  }

  /**
   * A record may be physical (table, view) but still considered to be
   * a work record if one or more of its keys do not have a value.
   */
  public boolean isEffectivelyAWorkRecord() {
    for (final Map.Entry<String, PTImmutableReference<PTBufferField>> entry
        : this.fieldRefs.entrySet()) {
      final PTBufferField fld = entry.getValue().deref();
      if (fld.getRecordFieldDefn().isKey() && !fld.getValue().isMarkedAsUpdated()) {
        return true;
      }
    }
    return false;
  }

  public void fireEvent(final PCEvent event,
      final FireEventSummary fireEventSummary) {

    // Only fire events on records in the component structure. Make
    // exception for SearchInit event.
    if (event != PCEvent.SEARCH_INIT
        && (this.recBuffer == null
            || !this.recBuffer.doesContainStructuralFields())) {
      return;
    }

    // Fire event on each field in this record.
    for (Map.Entry<String, PTImmutableReference<PTBufferField>> entry
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

    if (this.recBuffer == null || !this.recBuffer.doesContainStructuralFields()) {
      return;
    }

    // Run non-constant (from record) field default processing
    // on each field in this record.
    for (Map.Entry<String, PTImmutableReference<PTBufferField>> entry
        : this.fieldRefs.entrySet()) {
      // Note: callee will exit if field is not blank per fld def proc logic in PS.
      entry.getValue().deref().runNonConstantFieldDefaultProcessing(fldDefProcSummary);
      entry.getValue().deref().runConstantFieldDefaultProcessing(fldDefProcSummary);
    }

    // Run constant field default processing
    // on each field in this record.
    for (Map.Entry<String, PTImmutableReference<PTBufferField>> entry
        : this.fieldRefs.entrySet()) {
      // Note: callee will exit if field is not blank per fld def proc logic in PS.
    }

    // For any fields that are still blank, fire the FieldDefault event.
    // NOTE: This cannot be done in the PTBufferField call to runFieldDefaultProcessing,
    // because FieldDefault events only fire once every field has been assigned
    // a constant/record default (if defined).
    for (Map.Entry<String, PTImmutableReference<PTBufferField>> entry
        : this.fieldRefs.entrySet()) {
      final PTBufferField fld = entry.getValue().deref();
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

  public PTBufferRecord resolveContextualCBufferRecordReference(final String recName) {
    if (recName.equals(this.recDefn.RECNAME)) {
      return this;
    } else if (this.parentRow != null) {
      return this.parentRow.resolveContextualCBufferRecordReference(recName);
    }
    return null;
  }

  public PTReference<PTBufferField> resolveContextualCBufferRecordFieldReference(
      final String recName, final String fieldName) {
    if (recName.equals(this.recDefn.RECNAME)
        && this.hasField(fieldName)) {
      return this.getFieldRef(fieldName);
    } else if (this.parentRow != null) {
      return this.parentRow.resolveContextualCBufferRecordFieldReference(
          recName, fieldName);
    }
    return null;
  }

  public PTBufferRowset resolveContextualCBufferScrollReference(
      final PTScrollLiteral scrollName) {
    if (this.parentRow != null) {
      return this.parentRow.resolveContextualCBufferScrollReference(scrollName);
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

  @Override
  public String toString() {
    return "[BUFFER]" + super.toString();
  }
}
