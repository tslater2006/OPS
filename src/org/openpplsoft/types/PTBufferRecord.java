/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openpplsoft.buffers.ComponentBuffer;
import org.openpplsoft.buffers.RecordBuffer;
import org.openpplsoft.buffers.RecordFieldBuffer;
import org.openpplsoft.pt.*;
import org.openpplsoft.pt.pages.PgToken;
import org.openpplsoft.pt.peoplecode.PeopleCodeProg;
import org.openpplsoft.runtime.*;
import org.openpplsoft.sql.OPSResultSet;
import org.openpplsoft.sql.OPSStmt;
import org.openpplsoft.sql.StmtLibrary;
import org.openpplsoft.trace.*;

/**
 * Represents a PeopleTools record in the component buffer.
 */
public final class PTBufferRecord extends PTRecord<PTBufferRow, PTBufferField>
    implements ICBufferEntity {

  private static Logger log = LogManager.getLogger(PTBufferRecord.class.getName());

  private final RecordBuffer recBuffer;

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
        new TreeMap<Integer, PTImmutableReference<PTBufferField>>();
    int i = 1;
    for (final RecordField rf : this.recDefn.getExpandedFieldList()) {
      PTFieldTypeConstraint fldTc = new PTFieldTypeConstraint();

      try {
        PTImmutableReference<PTBufferField> newFldRef = null;

        // If this record field has a buffer associated with it, allocate the
        // field with that to give the field a reference to that buffer. Otherwise,
        // allocate the field with just its record field defn.
        if (this.recBuffer.hasRecordFieldBuffer(rf.getFldName())) {
          newFldRef = new PTImmutableReference<PTBufferField>(fldTc,
              fldTc.allocBufferField(this,
                  this.recBuffer.getRecordFieldBuffer(rf.getFldName())));
        } else {
          newFldRef
              = new PTImmutableReference<PTBufferField>(fldTc,
                  fldTc.allocBufferField(this, rf));
        }
        this.fieldRefs.put(rf.getFldName(), newFldRef);
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

  public void emitRecInScroll(final String indentStr) {

    if (PSDefn.isSystemRecord(this.recDefn.getRecName())) {
      return;
    }

    int keyrec = -1, keyfield = -1;
    if (this.isRelatedDisplayRecord()) {
      final PTBufferField dispCtrlFld = this.getDisplayControlField();
      keyrec = dispCtrlFld.getParentRecord()
          .getIndexPositionOfThisRecordInParentRow();
      keyfield = dispCtrlFld.getIndexPositionOfThisFieldInParentRecord();
    }

    TraceFileVerifier.submitEnforcedEmission(new RecInScroll(
        indentStr, this.recDefn.getRecName(), keyrec, keyfield));
  }

  public PTBufferField getDisplayControlField() {

    if (!this.isRelatedDisplayRecord()) {
      throw new OPSVMachRuntimeException("Illegal call to getDisplayControlField(); this "
          + "record is not a related display record.");
    }

    /*
     * All fields on a related display record are tied to
     * the same display control field. Therefore, we can get
     * the display control field by looking at just the first field
     * on the record, and then at the first rel disp field token
     * attached to that field.
     */
    final RecordFieldBuffer relDispFldBuf =
        this.recBuffer.getFieldBuffers().get(0);
    final PgToken relDispFldTok = relDispFldBuf.getPageFieldToks().get(0);
    return this.getParentRow().findDisplayControlField(relDispFldTok);
  }

  public boolean isRelatedDisplayRecord() {
    return this.recBuffer.isRelatedDisplayRecBuffer();
  }

  private boolean isPrimaryRecordInRowset() {
    return this.parentRow.getParentRowset().getPrimaryRecDefn() == this.recDefn;
  }

  public void emitScrolls(final ScrollEmissionContext ctxFlag, final int indent) {

    if (PSDefn.isSystemRecord(this.recDefn.getRecName())) {
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
    final int scrollLevel = this.determineScrollLevel();
    if (ctxFlag != ScrollEmissionContext.SEARCH_RESULTS) {
      if (this.recDefn.isDerivedWorkRecord()) {
        flagStr += " work";
      } else {

        if (this.isEffectivelyAWorkRecord()) {
          log.debug("Effectively a work record: {}", this.recDefn.getRecName());
          flagStr += " work";
        }

        if (this.isPrimaryRecordInRowset() && this.parentRow.isNew()) {
          flagStr += " new";
        }

        if (this.parentRow.needsInit()) {
          flagStr += " needsinit";
        }

        if (this.isPrimaryRecordInRowset() && this.parentRow.isDummy()) {
          flagStr += " dummy";
        }

        if (this.isRelatedDisplayRecord()) {
          flagStr += " reldisp";
        } else if (scrollLevel == 0) {
          flagStr += " lvl0";
        }

      }
    }
    flagStr = flagStr.trim();

    TraceFileVerifier.submitEnforcedEmission(
        new CRecBuf(indentStr, this.recDefn.getRecName(),
            this.recDefn.getExpandedFieldList().size(), flagStr));

    for (final Map.Entry<String, PTImmutableReference<PTBufferField>> entry
        : this.fieldRefs.entrySet()) {
      entry.getValue().deref().emitScrolls(ctxFlag, indent);
    }
  }

  /**
   * A record may be physical (table, view) but still considered to be
   * a work record if one or more of its keys do not have a value.
   */
  public boolean isEffectivelyAWorkRecord() {

    if (this.isPrimaryRecordInRowset()) {
      return false;
    }

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
              recFieldDefn.getRecName(), recFieldDefn.getFldName());
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

  public void runRelatedDisplayProcessing() {
    if (!this.isRelatedDisplayRecord()) {
      return;
    }

    if (!PSDefn.isSystemRecord(this.getRecDefn().getRecName())) {
      final PTBufferField dispCtrlFld = this.getDisplayControlField();
      TraceFileVerifier.submitEnforcedEmission(
          new RelDispFldStart(new RecFldName(dispCtrlFld.getRecordFieldDefn())));

      if (!dispCtrlFld.getValue().isBlank()) {
        boolean hasEmittedKeylistGenStart = false;
        for (final PTBufferField fld : this.getAllFields()) {
          final String fldName = fld.getRecordFieldDefn().getFldName();

          if (!hasEmittedKeylistGenStart) {
            TraceFileVerifier.submitEnforcedEmission(new KeylistGenStart());
            hasEmittedKeylistGenStart = true;
          }

          if (fld.getRecordFieldDefn().isKey()
              && !dispCtrlFld.getParentRecord().hasField(fldName)) {

            TraceFileVerifier.submitEnforcedEmission(new KeylistGenDetectedKey(
                fldName));
            TraceFileVerifier.submitEnforcedEmission(new KeylistGenFindingKey(
                fld.getRecordFieldDefn().getRecFldName()));

            /**
             * TODO: IMPORTANT NOTE: I am assuming that "key buffer"
             * means the record on which the display control field exists;
             * this might actually mean the search record, in which case the
             * conditional must be changed.
             */
            if (dispCtrlFld.getParentRecord().hasKeyField(fldName)) {
              throw new OPSVMachRuntimeException("TODO: Field found in key "
                  + "buffers; need to handle this.");
            } else {
              TraceFileVerifier.submitEnforcedEmission(
                  new KeylistGenNotInKeyBuffer());
              TraceFileVerifier.submitEnforcedEmission(
                  new KeylistGenSearchingCompBuffers(fldName));
              TraceFileVerifier.submitEnforcedEmission(
                  new KeylistGenScanningLevel(0));

              Optional<PTBufferField> foundCBufferField = Optional.empty();
              for (final PTBufferRecord rec : ComponentBuffer
                  .getLevelZeroRowset().getRow(1).getRecordList()) {
                if(!rec.isEffectivelyAWorkRecord()) {

                  TraceFileVerifier.submitEnforcedEmission(
                      new KeylistGenScanningRecord(rec.getRecName(), fldName));

                  if (rec.hasField(fldName)
                      && rec.getField(fldName).isPresentInScrollEmissions(
                          ScrollEmissionContext.AFTER_SCROLL_SELECT)) {
                    foundCBufferField = Optional.of(rec.getField(fldName));
                    TraceFileVerifier.submitEnforcedEmission(
                        new KeylistGenFoundInRecord(rec.getRecName(), fldName));
                    break;
                  }
                }
              }

              if (foundCBufferField.isPresent()) {
                final PTPrimitiveType val = foundCBufferField.get().getValue();
                TraceFileVerifier.submitEnforcedEmission(
                    new KeylistGenFoundInCBuffer(val.readAsString()));

                /*
                 * I am aware that this directly contradicts the emission above,
                 * but that's what PS does.
                 */
                if (val.isBlank()) {
                  TraceFileVerifier.submitEnforcedEmission(
                      new KeylistGenNotInCompBuffers());
                  TraceFileVerifier.submitEnforcedEmission(
                      new KeylistGenNotInCompKeylist());

                  try (final OPSStmt ostmt =
                          StmtLibrary.generateGenericTableQuery(
                              DefnCache.getRecord("INSTALLATION"));
                       final OPSResultSet rs = ostmt.executeQuery()) {
                    if (rs.hasColumnNamed(fldName)) {
                      throw new OPSVMachRuntimeException("TODO: Found keylist "
                          + "field on INSTALLATION table, need to handle this.");
                    }
                  }

                  TraceFileVerifier.submitEnforcedEmission(
                      new KeylistGenNotInInstallRecord());

                  try (final OPSStmt ostmt =
                          StmtLibrary.generateGenericTableQuery(
                              DefnCache.getRecord("PSOPTIONS"));
                       final OPSResultSet rs = ostmt.executeQuery()) {
                    if (rs.hasColumnNamed(fldName)) {
                      throw new OPSVMachRuntimeException("TODO: Found keylist "
                          + "field on PSOPTIONS table, need to handle this.");
                    }
                  }

                  TraceFileVerifier.submitEnforcedEmission(
                      new KeylistGenNotInPSOPTIONS());
                }
              }
            }
          }
        }
      }

      TraceFileVerifier.submitEnforcedEmission(
          new RelDispFldComplete(new RecFldName(dispCtrlFld.getRecordFieldDefn())));
    }
  }

  public PTBufferRecord resolveContextualCBufferRecordReference(final String recName) {
    if (recName.equals(this.recDefn.getRecName())) {
      return this;
    } else if (this.parentRow != null) {
      return this.parentRow.resolveContextualCBufferRecordReference(recName);
    }
    return null;
  }

  public PTReference<PTBufferField> resolveContextualCBufferRecordFieldReference(
      final String recName, final String fieldName) {
    if (recName.equals(this.recDefn.getRecName())
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

  public boolean sharesSearchKeysWithSearchRecord() {
    final Set<String> searchRecSearchKeyFldNames =
        ComponentBuffer.getSearchRecord().getSearchKeyFieldNames();
    final Set<String> thisRecSearchKeyFldNames =
        this.getSearchKeyFieldNames();

    for (final String fldName : thisRecSearchKeyFldNames) {
      final PTBufferField fld = this.getField(fldName);
      if (fld.getRecordFieldBuffer() == null
          || fld.getRecordFieldBuffer().getPageFieldToks().size() == 0) {
        return false;
      }
    }
    return searchRecSearchKeyFldNames.equals(thisRecSearchKeyFldNames);
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

    final Record recDefn = DefnCache.getRecord(this.recDefn.getRecName());

    if (recDefn.hasARequiredKeyField() || recDefn.hasNoKeys()) {
      try (final OPSStmt ostmt = StmtLibrary.prepareFirstPassFillQuery(this);
           final OPSResultSet rs = ostmt.executeQuery()) {
        final int numCols = rs.getColumnCount();

        // NOTE: record may legitimately be empty.
        if (rs.next()) {
          rs.readIntoRecord(this);
        }
      }
    } else {
      log.debug("{} does not meet reqs for first pass fill; now searching "
          + "for keys that can be set.", recDefn.getRecName());
      final List<PTBufferField> flds = this.getAllFields();
      for (final PTBufferField fld : flds) {
        final RecordField rf = fld.getRecordFieldDefn();
        if (rf.isKey()) {
          final Keylist keylist = new Keylist();
          this.generateKeylist(rf.getFldName(), keylist);
          log.debug("Keylist for field {}.{}: {}",
              rf.getRecName(), rf.getFldName(), keylist);
          if (keylist.hasNonBlankValue()) {
            final PTPrimitiveType keyVal =
                keylist.getFirstNonBlankField().getValue();
            log.debug("Performing a system copy to {}.{} with value {}.",
                rf.getRecName(), rf.getFldName(), keyVal);
            fld.getValue().systemCopyValueFrom(keyVal);
          } else {
            log.debug("No suitable value found for {}.{} in keylist.",
                rf.getRecName(), rf.getFldName());
          }
        }
      }
    }
  }

  @Override
  public String toString() {
    return "[BUFFER]" + super.toString();
  }
}
