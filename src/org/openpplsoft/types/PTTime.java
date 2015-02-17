/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Implementation of the PeopleTools time data type.
 */
public final class PTTime extends PTPrimitiveType<String> {

  private static PTTypeConstraint<PTTime> timeTc;

  static {
    timeTc = new PTTypeConstraint<PTTime>(PTTime.class);
  }

  public PTTime(PTTypeConstraint origTc) {
    super(origTc);
  }

  public static PTTypeConstraint<PTTime> getTc() {
    return timeTc;
  }

  @Override
  public void copyValueFrom(final PTPrimitiveType src) {
    throw new OPSDataTypeException("copyValueFrom is not yet supported.");
  }

  @Override
  public void setBlank() {
    // default value is current time.
    this.value = new SimpleDateFormat("HH:mm:ss")
        .format(Calendar.getInstance().getTime());
  }

  @Override
  public boolean isBlank() {
    return (this.value == null);
  }

  @Override
  public PTBoolean isEqual(final PTPrimitiveType op) {
    throw new OPSDataTypeException("isEqual not implemented for "
        + "Date.");
  }

  @Override
  public String readAsString() {
    if (this.value == null) {
      return null;
    }
    return this.value.toString();
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
    if (this.value.compareTo(((PTTime) op).read()) >= 0) {
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
    if (this.value.compareTo(((PTTime) op).read()) <= 0) {
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
}
