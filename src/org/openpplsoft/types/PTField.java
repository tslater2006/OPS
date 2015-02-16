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

public abstract class PTField<R extends PTRecord> extends PTObjectType {

  private static Logger log = LogManager.getLogger(PTField.class.getName());

  private static Map<String, Method> ptMethodTable;

  protected final RecordField recFieldDefn;
  protected final PTImmutableReference<PTPrimitiveType> valueRef;
  protected final PTImmutableReference<PTBoolean> visiblePropertyRef,
      displayOnlyPropertyRef;
  protected final PTImmutableReference<PTString> fldNamePropertyRef;

  protected R parentRecord;
  protected RecordFieldBuffer recFieldBuffer;
  protected boolean isGrayedOut;

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

  public PTField(final PTFieldTypeConstraint origTc,
      final RecordField recFieldDefn) {
    super(origTc);
    this.recFieldDefn = recFieldDefn;

    final PTTypeConstraint valueTc = this.recFieldDefn
        .getTypeConstraintForUnderlyingValue();
    try {
      /*
       * Initialize read/write properties.
       */
      this.valueRef
          = new PTImmutableReference<PTPrimitiveType>(valueTc,
              (PTPrimitiveType) valueTc.alloc());
      this.visiblePropertyRef
          = new PTImmutableReference<PTBoolean>(
              PTBoolean.getTc(), new PTBoolean(true));
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
              PTString.getTc(), new PTString(this.recFieldDefn.getFldName()));
      this.fldNamePropertyRef.deref().setReadOnly();

    } catch (final OPSTypeCheckException opstce) {
      throw new OPSVMachRuntimeException(opstce.getMessage(), opstce);
    }
  }

  public R getParentRecord() {
    return this.parentRecord;
  }

  public PTPrimitiveType getValue() {
    return this.valueRef.deref();
  }

  public RecordField getRecordFieldDefn() {
    return this.recFieldDefn;
  }

  public void setBlank() {
    valueRef.deref().setBlank();
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

  public void hide() {
    this.visiblePropertyRef.deref().write(false);
  }

  public void unhide() {
    this.visiblePropertyRef.deref().write(true);
  }

  public boolean isHidden() {
    return !this.visiblePropertyRef.deref().read();
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
      throw new OPSVMachRuntimeException("Expected no args to SetDefault.");
    }
    this.setBlank();
  }

  public void PT_GetLongLabel() {
    final List<PTType> args = Environment.getArgsFromCallStack();
    if (args.size() != 1
        || !(args.get(0) instanceof PTString)) {
      throw new OPSVMachRuntimeException("Expected single string arg to GetLongLabel.");
    }

    final String labelId = ((PTString) args.get(0)).read();
    final FieldLabel label = this.recFieldDefn.getLabelById(labelId);

    // If label does not exist, PT documentation indicates that a "Null"
    // (blank) string should be returned.
    if (label == null) {
      Environment.pushToCallStack(new PTString(""));
    } else {
      Environment.pushToCallStack(new PTString(label.getLongName()));
    }
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(":").append(recFieldDefn.getFldName());
    b.append(",valueRef=").append(valueRef.toString());
    return b.toString();
  }
}
