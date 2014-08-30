/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.text.SimpleDateFormat;

import java.util.Calendar;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.openpplsoft.runtime.*;

/**
 * Implementation of the PeopleTools time data type.
 */
public final class PTTime extends PTPrimitiveType<String> {

  private static final Type staticTypeFlag = Type.TIME;
  private String d;

  public PTTime() {
    super(staticTypeFlag,
        new PTTypeConstraint<PTTime>(PTTime.class));

    // default value is current time.
    this.d = new SimpleDateFormat("HH:mm:ss")
        .format(Calendar.getInstance().getTime());
  }

  @Override
  public String read() {
    return this.d;
  }

  @Override
  public String readAsString() {
    return this.d;
  }

  @Override
  public void write(final String newValue) {
    this.checkIsWriteable();
    this.d = newValue;
  }

  @Override
  public void systemWrite(final String newValue) {
    this.d = newValue;
  }

  @Override
  public void copyValueFrom(final PTPrimitiveType src) {
    throw new EntDataTypeException("copyValueFrom is not yet supported.");
  }

  @Override
  public PTPrimitiveType add(final PTPrimitiveType op) {
    throw new EntDataTypeException("add() not supported.");
  }

  @Override
  public PTPrimitiveType subtract(final PTPrimitiveType op) {
    throw new OPSVMachRuntimeException("subtract() not supported.");
  }

  @Override
  public PTPrimitiveType mul(PTPrimitiveType op) {
    throw new OPSVMachRuntimeException("mul() not supported.");
  }

  @Override
  public PTPrimitiveType div(PTPrimitiveType op) {
    throw new OPSVMachRuntimeException("div() not supported.");
  }

  @Override
  public void setDefault() {
    this.d = null;
  }

  @Override
  public PTBoolean isEqual(final PTPrimitiveType op) {
    throw new EntDataTypeException("isEqual not implemented for "
        + "Date.");
  }

  @Override
  public PTBoolean isGreaterThan(final PTPrimitiveType op) {
    throw new EntDataTypeException("isGreaterThan not implemented for "
        + "Date.");
  }

  @Override
  public PTBoolean isGreaterThanOrEqual(final PTPrimitiveType op) {
    if (!(op instanceof PTTime)) {
      throw new EntDataTypeException("Expected op to be PTTime.");
    }
    if (this.d.compareTo(((PTTime) op).read()) >= 0) {
      return Environment.TRUE;
    }
    return Environment.FALSE;
  }

  @Override
  public PTBoolean isLessThan(final PTPrimitiveType op) {
    throw new EntDataTypeException("isLessThan is not supported for "
        + "Date.");
  }

  @Override
  public PTBoolean isLessThanOrEqual(final PTPrimitiveType op) {
    if (!(op instanceof PTTime)) {
      throw new EntDataTypeException("Expected op to be PTTime.");
    }
    if (this.d.compareTo(((PTTime) op).read()) <= 0) {
      return Environment.TRUE;
    }
    return Environment.FALSE;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof PTTime)) {
      return false;
    }

    final PTTime other = (PTTime) obj;
    if (this.read().equals(other.read())) {
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int HBC_INITIAL = 109, HBC_MULTIPLIER = 67;
    return new HashCodeBuilder(HBC_INITIAL, HBC_MULTIPLIER)
        .append(this.read()).toHashCode();
  }

  @Override
  public boolean typeCheck(final PTType a) {
    return (a instanceof PTTime
        && this.getType() == a.getType());
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",d=").append(this.d);
    return b.toString();
  }
}
