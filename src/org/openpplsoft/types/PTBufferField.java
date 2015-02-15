/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.lang.reflect.Method;
import java.util.*;
import java.sql.*;

import org.openpplsoft.runtime.*;
import org.openpplsoft.buffers.*;
import org.openpplsoft.pt.*;
import org.openpplsoft.pt.pages.*;
import org.openpplsoft.sql.*;
import org.openpplsoft.trace.*;
import org.openpplsoft.pt.peoplecode.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PTBufferField extends PTField<PTBufferRecord>
    implements ICBufferEntity {

  private static Logger log = LogManager.getLogger(PTBufferField.class.getName());

  private static Map<String, Method> ptMethodTable;
  private boolean isMarkedAsUpdated;

  private List<DropDownItem> dropDownList = new ArrayList<>();
  private boolean isMarkedAsDefaulted;

  private class DropDownItem {
    private String code, desc;
    public DropDownItem(final String c, final String d) {
      this.code = c;
      this.desc = d;
    }
  }

  static {
    final String PT_METHOD_PREFIX = "PT_";

    ptMethodTable = new HashMap<String, Method>();
    final Class[] classes = new Class[]{PTField.class, PTBufferField.class};

    for (final Class cls : classes) {
      final Method[] methods = cls.getMethods();
      for (final Method m : methods) {
        if (m.getName().indexOf(PT_METHOD_PREFIX) == 0) {
          ptMethodTable.put(m.getName().substring(
              PT_METHOD_PREFIX.length()), m);
        }
      }
    }
  }

  public PTBufferField(final PTFieldTypeConstraint origTc,
      final PTBufferRecord pRecord, final RecordField rfd) {
    super(origTc, rfd);
    this.parentRecord = pRecord;
  }

  public PTBufferField(final PTFieldTypeConstraint origTc,
      final PTBufferRecord pRecord, final RecordFieldBuffer recFldBuffer) {
    super(origTc, recFldBuffer.getRecFldDefn());
    this.parentRecord = pRecord;
    this.recFieldBuffer = recFldBuffer;
  }

  /**
   * NOTE: This applies to the field, not the value within
   * the field; I realize this is confusing but am in the
   * middle of trying to determine exactly where PeopleSoft
   * draws the line.
   */
  private void markAsUpdated() {
    this.isMarkedAsUpdated = true;
  }

  public RecordFieldBuffer getRecordFieldBuffer() {
    return this.recFieldBuffer;
  }

  public int getIndexPositionOfThisFieldInParentRecord() {
    return this.parentRecord.getIndexPositionOfField(this);
  }

  /**
   * The meaning of "Used" here refers to the way in which PeopleSoft
   * means it during scroll emissions; that is,
   * to describe a field referenced by one or more page fields.
   */
  private boolean isUsed() {
    return this.recFieldBuffer != null
        && this.recFieldBuffer.getPageFieldToks().size() > 0;
  }

  public void emitScrolls(final ScrollEmissionContext ctxFlag, final int indent) {

    // If this is a reldisp record and this record field is not represented in the PRM,
    // do not emit it during emission of scrolls.
  /*  if(this.parentRecord.getRecBuffer().isRelatedDisplayRecBuffer()
        && this.recFieldBuffer != null
        && !this.recFieldBuffer.isRelatedDisplayField()
        && !this.recFieldBuffer.isDisplayControlField()
        && !ComponentBuffer.hasPRMEntry(new RecFldName(
            this.recFieldDefn.getRecName(), this.recFieldDefn.getFldName()))) {
      return;
    }*/

    // For all non-reldisp record fields, do not emit the record field
    // if it does not have a record field buffer.
    if (!this.parentRecord.getRecBuffer().isRelatedDisplayRecBuffer()
        && this.recFieldBuffer == null) {
      log.debug("[1] Skipping field: {}", this.recFieldDefn.getFldName());
      return;
    }

    // If we are emitting scrolls for search results, only emit key fields
    // that have been updated.
    if (ctxFlag == ScrollEmissionContext.SEARCH_RESULTS
        && (!this.recFieldDefn.isKey()
            || !this.getValue().isMarkedAsUpdated())) {
      log.debug("[2] Skipping field: {}", this.recFieldDefn.getFldName());
      return;
    }

    // If we are emitting scrolls in any context other than search
    // results and the parent record is effectively a work record, only emit this
    // field if it has one or more page field tokens.
    if (ctxFlag != ScrollEmissionContext.SEARCH_RESULTS
        && this.parentRecord.isEffectivelyAWorkRecord()
        && !this.parentRecord.getRecBuffer().isRelatedDisplayRecBuffer()
        && (this.recFieldBuffer == null
            || this.recFieldBuffer.getPageFieldToks().size() == 0)) {
      log.debug("[3] Skipping field: {}", this.recFieldDefn.getFldName());
      return;
    }

    log.debug("Field {} key? {}", this.recFieldDefn.getFldName(), this.recFieldDefn.isKey());
    log.debug("Field {} recFieldBuffer null? {}",
        this.parentRecord.getRecDefn().getRecName(), this.recFieldBuffer == null);
    log.debug("Field {}.{} has PRM entry? {}",
        this.recFieldDefn.getRecName(),
        this.recFieldDefn.getFldName(),
        ComponentBuffer.hasPRMEntry(new RecFldName(
                this.recFieldDefn.getRecName(), this.recFieldDefn.getFldName())));
    if (ctxFlag != ScrollEmissionContext.SEARCH_RESULTS
        && this.parentRecord.isEffectivelyAWorkRecord()
        && this.parentRecord.getRecBuffer().isRelatedDisplayRecBuffer()
        && !this.recFieldDefn.getFldName().equals("EFF_STATUS")
        && !this.recFieldDefn.isKey()
        && (this.recFieldBuffer == null
            || (!this.recFieldBuffer.isRelatedDisplayField()
                && !this.recFieldBuffer.isDisplayControlField()))
        // NOTE: IT IS VERY IMPORTANT that you use the name of the parent record,
        // and not that of the record field defn, due to possibility that field
        // is defined on a subrecord of the parent record.
        && !ComponentBuffer.hasPRMEntry(new RecFldName(
                this.parentRecord.getRecDefn().getRecName(),
                this.recFieldDefn.getFldName()))) {
      log.debug("[4] Skipping field: {}", this.recFieldDefn.getFldName());
      return;
    }

    String indentStr = "";
    for (int i = 0; i < indent; i++) {
      indentStr += "|  ";
    }

    String flagStr = "";
    if (this.recFieldDefn.isKey()) {
      flagStr += " key";
    }

    if (this.recFieldDefn.isAlternateSearchKey()) {
      flagStr += " altkey";
    }

    if (this.recFieldBuffer != null
        && this.recFieldBuffer.isDisplayControlField()) {
      flagStr += " relkey";
    } else if (this.recFieldDefn.getFldName().equals("EFFDT")
        && this.determineScrollLevel() == 0) {
      flagStr += " relkey";
    }

    if (this.getValue().isMarkedAsUpdated()
        && (!this.isMarkedAsDefaulted || this.isMarkedAsUpdated)
        && (this.parentRecord.getRecDefn().isDerivedWorkRecord()
            || this.parentRecord.isEffectivelyAWorkRecord())
        && ctxFlag != ScrollEmissionContext.SEARCH_RESULTS) {
      flagStr += " updated";
    }

    if (this.isHidden()
        && ctxFlag != ScrollEmissionContext.SEARCH_RESULTS) {
      flagStr += " hide";
    }

    if (this.isUsed()
        || this.parentRecord.getRecBuffer().isRelatedDisplayRecBuffer()
        || ctxFlag == ScrollEmissionContext.SEARCH_RESULTS) {
      flagStr += " used";
    }

    TraceFileVerifier.submitEnforcedEmission(
        new CFldBuf(indentStr + "  ", this.recFieldDefn.getFldName(),
            this.getValue().readAsString(), flagStr));
  }

  public void fireEvent(final PCEvent event,
      final FireEventSummary fireEventSummary) {

    // PeopleCode events fire only on entities in the component buffer
    // (make exception for SearchInit, as search record does not have a buffer).
    if (this.recFieldBuffer == null && event != PCEvent.SEARCH_INIT) {
      return;
    }

    // If a Record PeopleCode program has been written for this event, run it now.
    final PeopleCodeProg recProg = this.recFieldDefn.getProgramForEvent(event);
    if (recProg != null && recProg.hasAtLeastOneStatement()) {
      final ExecContext eCtx = new ProgramExecContext(recProg,
          this.determineScrollLevel(), this.determineRowIndex());
      // Pass this field to the supervisor as the component buffer context.
      final InterpretSupervisor interpreter = new InterpretSupervisor(eCtx, this);
      interpreter.run();
      fireEventSummary.incrementNumEventProgsExecuted();
    }

    // If a Component PeopleCode program has been written for this event, run it now.
    final PeopleCodeProg compProg = ComponentBuffer.getComponentDefn()
        .getProgramForRecordFieldEvent(event, this.recFieldDefn);
    if (compProg != null && compProg.hasAtLeastOneStatement()) {
      final ExecContext eCtx = new ProgramExecContext(compProg,
            this.determineScrollLevel(), this.determineRowIndex());
      // Pass this field to the supervisor as the component buffer context.
      final InterpretSupervisor interpreter = new InterpretSupervisor(eCtx, this);
      interpreter.run();
      fireEventSummary.incrementNumEventProgsExecuted();
    }
  }

  public void runFieldDefaultProcessing(
      final FieldDefaultProcSummary fldDefProcSummary) {
    throw new OPSVMachRuntimeException("Illegal call to runFieldDefaultProcessing"
        + " on PTField; you must call the method for the appropriate type ("
        + "constant or non-constant) that you wish to run.");
  }

  public void runNonConstantFieldDefaultProcessing(
      final FieldDefaultProcSummary fldDefProcSummary) {

    // If this field is not in the component buffer (meaning it is not a field listed
    // in the component buffer structure), do not run field default proc on it.
    if (this.recFieldBuffer == null) {
//      log.debug("Skipping FldDefProc: {}.{}",
//          this.recFieldDefn.getRecName(), this.recFieldDefn.getFldName());
      return;
    }

    if (!this.recFieldDefn.hasDefaultNonConstantValue()) {
      return;
    }

    log.debug("Running FldDefProc: {}.{}",
          this.recFieldDefn.getRecName(), this.recFieldDefn.getFldName());

    // If field is not blank, no need to run field default proc on it.
    if (!this.getValue().isBlank()) {
      log.debug("Ignorning non-blank field during FldDefProc: {}", this);
      return;
    }

   if (this.recFieldDefn.isKey()) {
      final Keylist keylist = new Keylist();
      this.generateKeylist(keylist);
      log.debug("{}.{} has the following keylist during non-constant field def proc:\n{}",
          this.recFieldDefn.getRecName(), this.recFieldDefn.getFldName(), keylist);

      if (keylist.size() > 0 && keylist.isFirstValueNonBlank()) {
        log.debug("Ignoring key field {}.{} during "
            + "non-constant FldDefProc; key value exists in immediate context "
            + "so no need to default it.",
            this.recFieldDefn.getRecName(), this.recFieldDefn.getFldName());
        return;
      }
    }

    boolean preFldDefProcIsMarkedAsUpdated = this.getValue().isMarkedAsUpdated();
    final PCFldDefaultEmission fdEmission = new PCFldDefaultEmission(
        this.recFieldDefn.getRecName(), this.recFieldDefn.getFldName());

    final String defRecName = this.recFieldDefn.getDefaultRecName();
    final String defFldName = this.recFieldDefn.getDefaultFldName();
    final Record defRecDefn = DefnCache.getRecord(defRecName);

    OPSStmt ostmt = null;
    try {
      ostmt =
          StmtLibrary.generateNonConstantFieldDefaultQuery(defRecDefn, this);
    } catch (final OPSCBufferKeyLookupException opscbkle) {
      log.warn("Failed to generate non constant "
          + "field default query for field: " + this.recFieldDefn.getFldName()
          + "; a value for a key on the default record (" + defRecName + ") could "
          + "not be found in the component buffer. This is not an error, "
          + "just a warning that the field can't be defaulted at this time, "
          + "but may be defaulted on a future field def proc run if the key "
          + "is available at that time.");
      return;
    }

    log.debug("Querying {}.{} for default value for field {}.{}",
        defRecName, defFldName,
            this.recFieldDefn.getRecName(), this.recFieldDefn.getFldName());

    /*
     * Keep in mind that zero records may legitimately be returned here,
     * in which case the field will remain blank.
     */
    final OPSResultSet rs = ostmt.executeQuery();
    if (rs.next()) {
      log.debug("Defaulting to: {}", rs.getString(defFldName));

      // REMEMBER: defFldName is the name of a field on a *different* record;
      // we are reading that value into this field.
      rs.readNamedColumnIntoField(this, defFldName);

      if (rs.next()) {
        throw new OPSVMachRuntimeException(
            "Result set for default non constant field default query "
            + "returned multiple records; only expected one.");
      }

      fdEmission.setDefaultedValue(this.getValue().readAsString());
      fdEmission.setFromRecordFlag();
    }
    rs.close();
    ostmt.close();

    /*
     * Check if the field's value changed. If it did, an emission
     * must be made indicating as much.
     */
    if (!preFldDefProcIsMarkedAsUpdated && this.getValue().isMarkedAsUpdated()) {
      fldDefProcSummary.fieldWasChanged();
      this.markAsDefaulted();
      TraceFileVerifier.submitEnforcedEmission(fdEmission);
    } else if (this.getValue().isBlank()) {
      fldDefProcSummary.blankFieldWasSeen();
    }
  }

  public void runConstantFieldDefaultProcessing(
      final FieldDefaultProcSummary fldDefProcSummary) {

    // If this field is not in the component buffer (meaning it is not a field listed
    // in the component buffer structure), do not run field default proc on it.
    if (this.recFieldBuffer == null) {
//      log.debug("Skipping FldDefProc: {}.{}",
//          this.recFieldDefn.getRecName(), this.recFieldDefn.getFldName());
      return;
    }

    if (!this.recFieldDefn.hasDefaultConstantValue()) {
      return;
    }

    log.debug("Running FldDefProc: {}.{}",
          this.recFieldDefn.getRecName(), this.recFieldDefn.getFldName());

    // If field is not blank, no need to run field default proc on it.
    if (!this.getValue().isBlank()) {
      log.debug("Ignorning non-blank field during FldDefProc: {}|", this);
      return;
    }

    if (this.recFieldDefn.getFldName().equals("EFFDT")
        && this.recFieldDefn.isKey()
        && (this.parentRecord.getRecDefn().isTable()
            || this.parentRecord.getRecDefn().isView())) {

      final Keylist keylist = new Keylist();
      this.generateKeylist(keylist);
      log.debug("Keylist for {}.{} during constant def proc: {}",
          this.recFieldDefn.getRecName(), this.recFieldDefn.getFldName(), keylist.toString());

      if (keylist.hasNonBlankValue()) {
        log.debug("Ignorning key field {}.{} during "
            + "non-constant FldDefProc; key value exists in buffer context "
            + "so no need to default it.",
            this.recFieldDefn.getRecName(), this.recFieldDefn.getFldName());
        return;
      }
    }

    if (this.recFieldDefn.isKey() && !this.recFieldDefn.getFldName().equals("EFFDT")) {
      final Keylist keylist = new Keylist();
      this.generateKeylist(keylist);
      log.debug("Keylist for {}.{} during constant def proc: {}",
          this.recFieldDefn.getRecName(), this.recFieldDefn.getFldName(), keylist.toString());

      if (keylist.hasNonBlankValue()) {
        log.debug("Ignorning key field {}.{} during "
            + "non-constant FldDefProc; key value exists in buffer context "
            + "so no need to default it.",
            this.recFieldDefn.getRecName(), this.recFieldDefn.getFldName());
        return;
      } else {
        log.debug("No value found for key {}.{} during non-default fld proc, "
            + "will continue processing.",
            this.recFieldDefn.getRecName(), this.recFieldDefn.getFldName());
      }
    }

    boolean preFldDefProcIsMarkedAsUpdated = this.getValue().isMarkedAsUpdated();
    final PCFldDefaultEmission fdEmission = new PCFldDefaultEmission(
        this.recFieldDefn.getRecName(), this.recFieldDefn.getFldName());

      final String defValue = this.recFieldDefn.getDefaultFldName();
      final PTPrimitiveType fldValue = this.getValue();

      // First check if the value is actually a meta value (i.e., "%date")
      if (defValue.startsWith("%")) {
        if (defValue.equals("%date") && fldValue instanceof PTDateTime) {
          ((PTDateTime) fldValue).writeSYSDATE();
          fdEmission.setMetaValue(defValue);
        } else if (defValue.equals("%date") && fldValue instanceof PTDate) {
          ((PTDate) fldValue).writeSYSDATE();
          fdEmission.setMetaValue(defValue);
        } else {
          throw new OPSVMachRuntimeException("Unexpected defValue (" + defValue + ") "
              + "and field (" + fldValue + ") combination.");
        }
      // If not a meta value, interpret the value as a raw constant (i.e., "Y" or "9999").
      } else {
        if (fldValue instanceof PTString) {
          ((PTString) fldValue).write(defValue);
        } else if (fldValue instanceof PTChar && defValue.length() == 1) {
          ((PTChar) fldValue).write(defValue.charAt(0));
        } else {
          throw new OPSVMachRuntimeException("Expected PTString or PTChar for "
            + "field value while attempting to write field default: " + defValue);
        }
      }

      fdEmission.setDefaultedValue(fldValue.readAsString());
      fdEmission.setFromConstantFlag();

    /*
     * Check if the field's value changed. If it did, an emission
     * must be made indicating as much.
     */
    if (!preFldDefProcIsMarkedAsUpdated && this.getValue().isMarkedAsUpdated()) {
      fldDefProcSummary.fieldWasChanged();
      this.markAsDefaulted();
      TraceFileVerifier.submitEnforcedEmission(fdEmission);
    } else if (this.getValue().isBlank()) {
      fldDefProcSummary.blankFieldWasSeen();
    }
  }

  private void markAsDefaulted() {
    this.isMarkedAsDefaulted = true;
  }

  public PTBufferRecord resolveContextualCBufferRecordReference(final String recName) {
    if (this.parentRecord != null) {
      return this.parentRecord.resolveContextualCBufferRecordReference(recName);
    }
    return null;
  }

  public PTReference<PTBufferField> resolveContextualCBufferRecordFieldReference(
      final String recName, final String fieldName) {
    if (this.parentRecord != null) {
      return this.parentRecord.resolveContextualCBufferRecordFieldReference(
          recName, fieldName);
    }
    return null;
  }

  public PTBufferRowset resolveContextualCBufferScrollReference(
      final PTScrollLiteral scrollName) {
    if (this.parentRecord != null) {
      return this.parentRecord.resolveContextualCBufferScrollReference(scrollName);
    }
    return null;
  }

  public void generateKeylist(
      final String fieldName, final Keylist keylist) {
    throw new OPSVMachRuntimeException("Illegal call to find value for key "
        + "on PTField; you must call the overloaded version of this method.");
  }

  public void generateKeylist(final Keylist keylist) {
    if (this.parentRecord != null) {
      this.parentRecord.generateKeylist(
          this.recFieldDefn.getFldName(), keylist);
    }
  }

  public PTImmutableReference dotProperty(String s) {
    if(s.toLowerCase().equals("value")) {

      /*
       * Why is this check here and not in Record's PT_GetField, you may ask?
       * Furthermore, why are we only throwing an exception when attempting to
       * write to the Value of a field that is not in the component buffer (and not
       * Visible, DisplayOnly, etc.)?
       * Because apparently that's what PS does according to the tracefile.
       * This exception will be converted to a PCE in visitStmtAssign of the
       * interpreter.
       */
      if (this.parentRecord != null
          && this.parentRecord.getRecBuffer() != null
          && this.recFieldBuffer == null) {
        throw new OPSIllegalNonCBufferFieldAccessAttempt(
            this.recFieldDefn.getRecName(), this.recFieldDefn.getFldName());
      }
      return this.valueRef;
    } else if(s.toLowerCase().equals("visible")) {
      return this.visiblePropertyRef;
    } else if(s.toLowerCase().equals("name")) {
      return this.fldNamePropertyRef;
    } else if(s.toLowerCase().equals("displayonly")) {
      return this.displayOnlyPropertyRef;
    }
    return null;
  }

  public int determineRowIndex() {
    if (this.parentRecord != null
        && this.parentRecord.getParentRow() != null) {
      return this.parentRecord.getParentRow().getIndexOfThisRowInParentRowset();
    }

    throw new OPSVMachRuntimeException("Failed to determine ancestral row "
        + "index; path to parent record and/or row contains null somewhere.");
  }

  public int determineScrollLevel() {
    if (this.parentRecord != null) {
      return this.parentRecord.determineScrollLevel();
    }

    throw new OPSVMachRuntimeException("Failed to determine scroll level for "
        + "this field; parent record is null.");
  }

  @Override
  public Callable dotMethod(final String s) {
    if (ptMethodTable.containsKey(s)) {
      return new Callable(ptMethodTable.get(s), this);
    }
    return null;
  }

  public void PT_GetRelated() {
    final List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if (args.size() != 1 || !(args.get(0) instanceof PTRecordFieldSpecifier)) {
      throw new OPSVMachRuntimeException("Expected single PTRecordFieldSpecifier "
          + "arg to GetRelated.");
    }

    final PTRecordFieldSpecifier relDispRecFldSpecifier =
        ((PTRecordFieldSpecifier) args.get(0));

    if (this.recFieldBuffer == null) {
      throw new OPSVMachRuntimeException("Illegal call to GetRelated; this field "
          + "(" + this + ") has no record field buffer.");
    }

    if (this.recFieldBuffer.getOnlyPageFieldTok() == null) {
      throw new OPSVMachRuntimeException("Illegal call to GetRelated; this field "
          + "(" + this + ") has no underlying page token.");
    }

    final PgToken srcToken = this.recFieldBuffer.getOnlyPageFieldTok();
    if (!srcToken.isDisplayControl()) {
      throw new OPSVMachRuntimeException("Illegal call to GetRelated; this field's "
          + "underlying page token is not a display control field: " + srcToken);
    }

    PgToken desiredRelDispFieldTok = null;
    for (final PgToken relDispFieldTok : srcToken.getRelDispFieldToks()) {
      if (relDispFieldTok.getRecName().equals(relDispRecFldSpecifier.getRecName())
          && relDispFieldTok.getFldName().equals(relDispRecFldSpecifier.getFieldName())) {
        desiredRelDispFieldTok = relDispFieldTok;
        break;
      }
    }

    if (desiredRelDispFieldTok == null) {
      throw new OPSVMachRuntimeException("In GetRelated, unable to find "
          + "a related display token that matches the rel disp record field specifier "
          + "provided: " + relDispRecFldSpecifier);
    }

    final PTBufferRecord relDispRecord = this.parentRecord.getParentRow()
        .getRelatedDisplayRecordSet().getRecord(
            desiredRelDispFieldTok.getDispControlRecFieldName(),
                relDispRecFldSpecifier.getRecName());
    final PTBufferField relDispField = relDispRecord.getFieldRef(
        relDispRecFldSpecifier.getFieldName()).deref();

    /*
     * This check makes absolutely sure that the resolved field's underlying
     * page token is a reference to the desired token we saved a reference to
     * earlier. If they are equal, we have the related field.
     */
    if (relDispField.getRecordFieldBuffer().getOnlyPageFieldTok() !=
        desiredRelDispFieldTok) {
      throw new OPSVMachRuntimeException("In GetRelated, the resolved field's "
          + "underlying page token does not match the desired one.");
    }

    Environment.pushToCallStack(relDispField);
  }

  public void PT_ClearDropDownList() {
    final List<PTType> args = Environment.getArgsFromCallStack();
    if (args.size() != 0) {
      throw new OPSVMachRuntimeException("Expected no args to ClearDropDownList");
    }
    this.dropDownList.clear();
    this.markAsUpdated();
  }

  public void PT_AddDropDownItem() {

    final List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if (args.size() != 2
        || !(args.get(0) instanceof PTString)
        || !(args.get(1) instanceof PTString)) {
      throw new OPSVMachRuntimeException("Expected two PTString args "
          + "to AddDropDownItem.");
    }

    this.dropDownList.add(new DropDownItem(
        ((PTString) args.get(0)).readAsString(),
        ((PTString) args.get(1)).readAsString()));
  }

  @Override
  public String toString() {
    return "[BUFFER]" + super.toString();
  }
}
