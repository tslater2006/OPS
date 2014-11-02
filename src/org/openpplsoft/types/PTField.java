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
import org.openpplsoft.sql.*;
import org.openpplsoft.trace.*;
import org.openpplsoft.pt.peoplecode.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PTField extends PTObjectType implements ICBufferEntity {

  private static Logger log = LogManager.getLogger(PTField.class.getName());

  private static Map<String, Method> ptMethodTable;

  private PTRecord parentRecord;
  private RecordField recFieldDefn;
  private RecordFieldBuffer recFieldBuffer;
  private PTImmutableReference<PTPrimitiveType> valueRef;
  private PTImmutableReference<PTBoolean> visiblePropertyRef,
      displayOnlyPropertyRef;
  private PTImmutableReference<PTString> fldNamePropertyRef;
  private List<DropDownItem> dropDownList;
  private boolean isGrayedOut;

  private class DropDownItem {
    private String code, desc;
    public DropDownItem(final String c, final String d) {
      this.code = c;
      this.desc = d;
    }
  }

  static {
    final String PT_METHOD_PREFIX = "PT_";

    // cache pointers to PeopleTools Field methods.
    final Method[] methods = PTField.class.getMethods();
    ptMethodTable = new HashMap<String, Method>();
    for (Method m : methods) {
      if (m.getName().indexOf(PT_METHOD_PREFIX) == 0) {
        ptMethodTable.put(m.getName().substring(
            PT_METHOD_PREFIX.length()), m);
      }
    }
  }

  public PTField(final PTFieldTypeConstraint origTc, final PTRecord pRecord,
      final RecordField rfd) {
    super(origTc);
    this.parentRecord = pRecord;
    this.recFieldDefn = rfd;
    this.init();
  }

  public PTField(final PTFieldTypeConstraint origTc, final PTRecord pRecord,
      final RecordFieldBuffer recFldBuffer) {
    super(origTc);
    this.parentRecord = pRecord;
    this.recFieldDefn = recFldBuffer.getRecFldDefn();
    this.recFieldBuffer = recFldBuffer;
    this.init();
  }

  private void init() {
    this.dropDownList = new ArrayList<DropDownItem>();
    final PTTypeConstraint valueTc
        = recFieldDefn.getTypeConstraintForUnderlyingValue();

    try {
      /*
       * Initialize read/write properties.
       */
      this.valueRef
          = new PTImmutableReference<PTPrimitiveType>(valueTc,
              (PTPrimitiveType) valueTc.alloc());
      this.visiblePropertyRef
          = new PTImmutableReference<PTBoolean>(
              PTBoolean.getTc(), PTBoolean.getTc().alloc());
      // NOTE: Technically this is supposed to be defaulted to the value
      // specified in App Designer. Checking if the first bit of FIELDUSE in
      // PgToken is set to 1 will tell you if the field is display only. However,
      // at this time, I am not linking pages to fields. If you have issues with
      // logic related to DisplayOnly, you may need to associate fields with pages.
      this.displayOnlyPropertyRef
          = new PTImmutableReference<PTBoolean>(
              PTBoolean.getTc(), PTBoolean.getTc().alloc());

      /*
       * Initialize read-only properties.
       */
      this.fldNamePropertyRef
          = new PTImmutableReference<PTString>(
              PTString.getTc(), new PTString(this.recFieldDefn.FIELDNAME));
      this.fldNamePropertyRef.deref().setReadOnly();

    } catch (final OPSTypeCheckException opstce) {
      throw new OPSVMachRuntimeException(opstce.getMessage(), opstce);
    }
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

  public PTRecord getParentRecord() {
    return this.parentRecord;
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
//          this.recFieldDefn.RECNAME, this.recFieldDefn.FIELDNAME);
      return;
    }

    if (!this.recFieldDefn.hasDefaultNonConstantValue()) {
      return;
    }

    log.debug("Running FldDefProc: {}.{}",
          this.recFieldDefn.RECNAME, this.recFieldDefn.FIELDNAME);

    // If field is not blank, no need to run field default proc on it.
    if (!this.getValue().isBlank()) {
      log.debug("Ignorning non-blank field during FldDefProc: {}", this);
      return;
    }

   if (this.recFieldDefn.isKey()) {
      final Keylist keylist = new Keylist();
      this.generateKeylist(keylist);
      log.debug("{}.{} has the following keylist during non-constant field def proc:\n{}",
          this.recFieldDefn.RECNAME, this.recFieldDefn.FIELDNAME, keylist);

      if (keylist.size() > 0 && keylist.isFirstValueNonBlank()) {
        log.debug("Ignoring key field {}.{} during "
            + "non-constant FldDefProc; key value exists in immediate context "
            + "so no need to default it.",
            this.recFieldDefn.RECNAME, this.recFieldDefn.FIELDNAME);
        return;
      }
    }

    boolean preFldDefProcIsMarkedAsUpdated = this.getValue().isMarkedAsUpdated();
    final PCFldDefaultEmission fdEmission = new PCFldDefaultEmission(
        this.recFieldDefn.RECNAME, this.recFieldDefn.FIELDNAME);

    final String defRecName = this.recFieldDefn.DEFRECNAME;
    final String defFldName = this.recFieldDefn.DEFFIELDNAME;
    final Record defRecDefn = DefnCache.getRecord(defRecName);

    OPSStmt ostmt = null;
    try {
      ostmt =
          StmtLibrary.generateNonConstantFieldDefaultQuery(defRecDefn, this);
    } catch (final OPSCBufferKeyLookupException opscbkle) {
      log.warn("Failed to generate non constant "
          + "field default query for field: " + this.recFieldDefn.FIELDNAME
          + "; a value for a key on the default record (" + defRecName + ") could "
          + "not be found in the component buffer. This is not an error, "
          + "just a warning that the field can't be defaulted at this time, "
          + "but may be defaulted on a future field def proc run if the key "
          + "is available at that time.");
      return;
    }

    log.debug("Querying {}.{} for default value for field {}.{}",
        defRecName, defFldName,
            this.recFieldDefn.RECNAME, this.recFieldDefn.FIELDNAME);

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
//          this.recFieldDefn.RECNAME, this.recFieldDefn.FIELDNAME);
      return;
    }

    if (!this.recFieldDefn.hasDefaultConstantValue()) {
      return;
    }

    log.debug("Running FldDefProc: {}.{}",
          this.recFieldDefn.RECNAME, this.recFieldDefn.FIELDNAME);

    // If field is not blank, no need to run field default proc on it.
    if (!this.getValue().isBlank()) {
      log.debug("Ignorning non-blank field during FldDefProc: {}|", this);
      return;
    }

    if (this.recFieldDefn.FIELDNAME.equals("EFFDT")
        && this.recFieldDefn.isKey()
        && (this.parentRecord.getRecDefn().isTable()
            || this.parentRecord.getRecDefn().isView())) {

      final Keylist keylist = new Keylist();
      this.generateKeylist(keylist);
      log.debug("Keylist for {}.{} during constant def proc: {}",
          this.recFieldDefn.RECNAME, this.recFieldDefn.FIELDNAME, keylist.toString());

      if (keylist.hasNonBlankValue()) {
        log.debug("Ignorning key field {}.{} during "
            + "non-constant FldDefProc; key value exists in buffer context "
            + "so no need to default it.",
            this.recFieldDefn.RECNAME, this.recFieldDefn.FIELDNAME);
        return;
      }
    }

    if (this.recFieldDefn.isKey() && !this.recFieldDefn.FIELDNAME.equals("EFFDT")) {
      final Keylist keylist = new Keylist();
      this.generateKeylist(keylist);
      log.debug("Keylist for {}.{} during constant def proc: {}",
          this.recFieldDefn.RECNAME, this.recFieldDefn.FIELDNAME, keylist.toString());

      if (keylist.hasNonBlankValue()) {
        log.debug("Ignorning key field {}.{} during "
            + "non-constant FldDefProc; key value exists in buffer context "
            + "so no need to default it.",
            this.recFieldDefn.RECNAME, this.recFieldDefn.FIELDNAME);
        return;
      } else {
        log.debug("No value found for key {}.{} during non-default fld proc, "
            + "will continue processing.",
            this.recFieldDefn.RECNAME, this.recFieldDefn.FIELDNAME);
      }
    }

    boolean preFldDefProcIsMarkedAsUpdated = this.getValue().isMarkedAsUpdated();
    final PCFldDefaultEmission fdEmission = new PCFldDefaultEmission(
        this.recFieldDefn.RECNAME, this.recFieldDefn.FIELDNAME);

      final String defValue = this.recFieldDefn.DEFFIELDNAME;
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
      TraceFileVerifier.submitEnforcedEmission(fdEmission);
    } else if (this.getValue().isBlank()) {
      fldDefProcSummary.blankFieldWasSeen();
    }
  }

  public PTType resolveContextualCBufferReference(final String identifier) {
    if (this.parentRecord != null) {
      return this.parentRecord.resolveContextualCBufferReference(identifier);
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
          this.recFieldDefn.FIELDNAME, keylist);
    }
  }

  public void setBlank() {
    valueRef.deref().setBlank();
  }

  public PTPrimitiveType getValue() {
    return this.valueRef.deref();
  }

  public RecordField getRecordFieldDefn() {
    return this.recFieldDefn;
  }

  public PTImmutableReference dotProperty(String s) {
    if(s.toLowerCase().equals("value")) {
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

  /**
   * From PeopleBooks
   * (http://docs.oracle.com/cd/E38689_01/pt853pbr0/eng/pt/tpcl/langref_PeopleCodeBuilt-inFunctionsandLanguageConstructs-073e6a.html#SetDefault-073c69)
   * :
   * "Use the SetDefault function to set a field to a null value,
   * so that the next time default processing occurs, it is set
   * to its default value"
   * TODO(mquinn): THERE ARE CASES WHERE CALLING THIS METHOD WILL ABORT
   * CONTINUED EXECUTION OF THE CALLING FUNCTION / PROGRAM; you must
   * implement these cases in the future.
   */
  public void PT_SetDefault() {
    final List<PTType> args = Environment.getArgsFromCallStack();
    if (args.size() != 0) {
      throw new OPSVMachRuntimeException("Expected no args.");
    }
    this.setBlank();
  }

  public void PT_ClearDropDownList() {
    this.dropDownList.clear();
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

  /**
   * Calls to make a field read-only should make the
   * field's value read-only as well.
   */
  @Override
  public void setReadOnly() {
    super.setReadOnly();
    if(this.valueRef != null) {
      this.valueRef.setReadOnly();
    }
  }

  public RecordFieldBuffer getRecordFieldBuffer() {
    return this.recFieldBuffer;
  }

  public void grayOut() {
    this.isGrayedOut = true;
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder(super.toString());
    b.append(":").append(recFieldDefn.FIELDNAME);
    b.append(",valueRef=").append(valueRef.toString());
    return b.toString();
  }
}
