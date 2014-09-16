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

  private static PTTypeConstraint<PTTime> timeTc;

  private String d;

  static {
    timeTc = new PTTypeConstraint<PTTime>(PTTime.class);
  }

  public PTTime(PTTypeConstraint origTc) {
    super(origTc);

    // default value is current time.
    this.d = new SimpleDateFormat("HH:mm:ss")
        .format(Calendar.getInstance().getTime());
  }

  public static PTTypeConstraint<PTTime> getTc() {
    return timeTc;
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
    throw new OPSDataTypeException("copyValueFrom is not yet supported.");
  }

  @Override
  public void setDefault() {
    this.d = null;
  }

  @Override
  public PTBoolean isEqual(final PTPrimitiveType op) {
    throw new OPSDataTypeException("isEqual not implemented for "
        + "Date.");
  }

  @Override
  public PTBoolean isGreaterThan(final PTPrimitiveType op) {
    throw new OPSDataTypeException("isGreaterThan not implemented for "
        + "Date.");
  }

  @Override
  public PTBoolean isGreaterThanOrEqual(final PTPrimitiveType op) {
    if (!(op instanceof PTTime)) {
      throw new OPSDataTypeException("Expected op to be PTTime.");
    }
    if (this.d.compareTo(((PTTime) op).read()) >= 0) {
      return new PTBoolean(true);
    }
    return new PTBoolean(false);
  }

  @Override
  public PTBoolean isLessThan(final PTPrimitiveType op) {
    throw new OPSDataTypeException("isLessThan is not supported for "
        + "Date.");
  }

  @Override
  public PTBoolean isLessThanOrEqual(final PTPrimitiveType op) {
    if (!(op instanceof PTTime)) {
      throw new OPSDataTypeException("Expected op to be PTTime.");
    }
    if (this.d.compareTo(((PTTime) op).read()) <= 0) {
      return new PTBoolean(true);
    }
    return new PTBoolean(false);
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
    final int HCB_INITIAL = 109, HCB_MULTIPLIER = 67;
    return new HashCodeBuilder(HCB_INITIAL, HCB_MULTIPLIER)
        .append(this.read()).toHashCode();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",d=").append(this.d);
    return b.toString();
  }
}
