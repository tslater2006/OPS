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
  private PTImmutableReference<PTBoolean> visiblePropertyRef;

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
    final PTTypeConstraint valueTc
        = recFieldDefn.getTypeConstraintForUnderlyingValue();
    final PTTypeConstraint<PTBoolean> visibleTc
        = new PTTypeConstraint<PTBoolean>(PTBoolean.class);

    try {
      this.valueRef
          = new PTImmutableReference<PTPrimitiveType>(valueTc,
              (PTPrimitiveType) valueTc.alloc());
      this.visiblePropertyRef
          = new PTImmutableReference<PTBoolean>(visibleTc, visibleTc.alloc());
    } catch (final OPSTypeCheckException opstce) {
      throw new OPSVMachRuntimeException(opstce.getMessage(), opstce);
    }
  }

  public void fireEvent(final PCEvent event,
      final FireEventSummary fireEventSummary) {

    // FieldFormula events are fired only on fields with an associated buffer
    // in the component.
    if (event == PCEvent.FIELD_FORMULA && this.recFieldBuffer == null) {
      return;
    }

    // If a Record PeopleCode program has been written for this event, run it now.
    final PeopleCodeProg recProg = this.recFieldDefn.getProgramForEvent(event);
    if (recProg != null) {
      final ExecContext eCtx = new ProgramExecContext(recProg);
      // Pass this field to the supervisor as the component buffer context.
      final InterpretSupervisor interpreter = new InterpretSupervisor(eCtx, this);
      interpreter.run();
      fireEventSummary.incrementNumEventProgsExecuted();
    }

    // If a Component PeopleCode program has been written for this event, run it now.
    final PeopleCodeProg compProg = ComponentBuffer.getComponentDefn()
        .getProgramForRecordFieldEvent(event, this.recFieldDefn);
    if (compProg != null) {
      final ExecContext eCtx = new ProgramExecContext(compProg);
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

    // If this field is not in the component buffer (meaning it is not a field listed
    // in the component buffer structure), do not run field default proc on it.
    if (this.recFieldBuffer == null) {
//      log.debug("Skipping FldDefProc: {}.{}",
//          this.recFieldDefn.RECNAME, this.recFieldDefn.FIELDNAME);
      return;
    }

    log.debug("Running FldDefProc: {}.{}",
          this.recFieldDefn.RECNAME, this.recFieldDefn.FIELDNAME);

    // If field is not blank, no need to run field default proc on it.
    if (!this.getValue().isBlank()) {
      log.debug("Ignorning non-blank field during FldDefProc: {}.{}",
            this.recFieldDefn.RECNAME, this.recFieldDefn.FIELDNAME);
      return;
    }

    // If field is a key, its value will be based on the keys defined on
    // fields in higher scroll levels, so don't perform field default processing.
    // NOTE: I am making an exception here for EFFDT, I don't know at this time
    // whether this is accurate and/or complete.
    // TODO(mquinn): Keep this in mind.
    if(this.recFieldDefn.isKey() && !this.recFieldDefn.FIELDNAME.equals("EFFDT")) {
      log.debug("Ignoring key field during FldDefProc: {}.{}",
          this.recFieldDefn.RECNAME, this.recFieldDefn.FIELDNAME);
      return;
    }

    final PCFldDefaultEmission fdEmission = new PCFldDefaultEmission(
        this.recFieldDefn.RECNAME, this.recFieldDefn.FIELDNAME);
    boolean preFldDefProcIsMarkedAsUpdated = this.getValue().isMarkedAsUpdated();

    if (this.recFieldDefn.hasDefaultNonConstantValue()) {
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

      ResultSet rs = null;
      try {
        rs = ostmt.executeQuery();
        /*
         * Keep in mind that zero records may legitimately be returned here,
         * in which case the field will remain blank.
         */
        if (rs.next()) {
          log.debug("Will default to: {}", rs.getString(defFldName));
          throw new OPSVMachRuntimeException("TODO: This code has not been "
              + "run yet for a field that actually generates a record from "
              + "which to default (queries so far have returned 0 records; "
              + "need to read *defFldName* from resultset and write that to "
              + "the field. ALSO REMEMBER TO UNCOMMENT THE CODE BELOW.");
  /*           if (rs.next()) {
                throw new OPSVMachRuntimeException(
                  "Result set for default non constant field default query "
                    + "returned multiple records; only expected one.");
                  }*/
        }
      } catch (final java.sql.SQLException sqle) {
        throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
      } finally {
        try {
          if (rs != null) { rs.close(); }
          if (ostmt != null) { ostmt.close(); }
        } catch (final java.sql.SQLException sqle) {
          log.warn("Unable to close rs and/or ostmt in finally block.");
        }
      }
    } else if (this.recFieldDefn.hasDefaultConstantValue()) {
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
      fdEmission.setConstantFlag();
    } else {
      // fireEvent() returns true if prog was run, false otherwise.
      final FireEventSummary summary = new FireEventSummary();
      this.fireEvent(PCEvent.FIELD_DEFAULT, summary);
      if(summary.getNumEventProgsExecuted() > 0) {
        fdEmission.setDefaultedValue("from peoplecode");
      } else {
        return;
      }
    }

    /*
     * At this point, some form of field default processing was executed, but
     * it may not have actually changed the field's value. If it did, an emission
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

  public PTPrimitiveType findValueForKeyInCBufferContext(
      final String fieldName) throws OPSCBufferKeyLookupException {
    throw new OPSVMachRuntimeException("Illegal call to find value for key "
        + "on PTField; you must call the overloaded version of this method that "
        + "lacks arguments.");
  }

  public PTPrimitiveType findValueForKeyInCBufferContext()
      throws OPSCBufferKeyLookupException {
    if (this.parentRecord != null) {
      return this.parentRecord.findValueForKeyInCBufferContext(
          this.recFieldDefn.FIELDNAME);
    }
    return null;
  }

  public void setDefault() {
    valueRef.deref().setDefault();
  }

  public PTPrimitiveType getValue() {
    return this.valueRef.deref();
  }

  public RecordField getRecordFieldDefn() {
    return this.recFieldDefn;
  }

  public PTImmutableReference dotProperty(String s) {
    if(s.equals("Value")) {
      return this.valueRef;
    } else if(s.equals("Visible")) {
      return this.visiblePropertyRef;
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
   * Implements the .SetDefault PeopleCode method for field objects.
   */
  public void PT_SetDefault() {
    final List<PTType> args = Environment.getArgsFromCallStack();
    if (args.size() != 0) {
      throw new OPSVMachRuntimeException("Expected no args.");
    }
    this.setDefault();
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

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder(super.toString());
    b.append(":").append(recFieldDefn.FIELDNAME);
    b.append(",valueRef=").append(valueRef.toString());
    return b.toString();
  }
}
