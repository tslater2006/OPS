/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.lang.reflect.Method;

import org.openpplsoft.buffers.*;
import org.openpplsoft.pt.*;
import org.openpplsoft.pt.peoplecode.*;
import java.util.*;
import org.openpplsoft.runtime.*;

public final class PTField extends PTObjectType implements ICBufferEntity {

  private static Map<String, Method> ptMethodTable;

  private PTRecord parentRecord;
  private RecordField recFieldDefn;
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

  public PTField(final PTFieldTypeConstraint origTc, final PTRecord pRecord, final RecordField rfd) {
    super(origTc);
    this.parentRecord = pRecord;
    this.recFieldDefn = rfd;

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

  public void fireEvent(final PCEvent event) {

    // If a Record PeopleCode program has been written for this event, run it now.
    final PeopleCodeProg recProg = this.recFieldDefn.getProgramForEvent(event);
    if (recProg != null) {
      final ExecContext eCtx = new ProgramExecContext(recProg);
      // Pass this field to the supervisor as the component buffer context.
      final InterpretSupervisor interpreter = new InterpretSupervisor(eCtx, this);
      interpreter.run();
    }

    // If a Component PeopleCode program has been written for this event, run it now.
    final PeopleCodeProg compProg = ComponentBuffer.getComponentDefn()
        .getProgramForRecordFieldEvent(event, this.recFieldDefn);
    if (compProg != null) {
      final ExecContext eCtx = new ProgramExecContext(compProg);
      // Pass this field to the supervisor as the component buffer context.
      final InterpretSupervisor interpreter = new InterpretSupervisor(eCtx, this);
      interpreter.run();
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
    throw new OPSVMachRuntimeException("Did not expect key lookup call on PTField; "
        + "if possible, this call should be made on the parent record, rather than "
        + "implemented here, altho doing so would just pass the request up "
        + "to the parent record.");
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
