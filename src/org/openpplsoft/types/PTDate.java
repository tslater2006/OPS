/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.*;

import java.text.SimpleDateFormat;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.openpplsoft.runtime.*;

public final class PTDate extends PTPrimitiveType<String> {

  private static PTTypeConstraint<PTDate> dateTc;
  private static String defaultDateOverride;

  static {
    dateTc = new PTTypeConstraint<PTDate>(PTDate.class);
  }

  public PTDate(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public static PTTypeConstraint<PTDate> getTc() {
    return dateTc;
  }

  public static void overrideDefaultDate(final String d) {
    defaultDateOverride = d;
  }

  public void copyValueFrom(PTPrimitiveType src) {
    if (!(src instanceof PTDate)) {
      throw new OPSDataTypeException("Expected src to be PTDate.");
    }
    this.write(((PTDate) src).read());
  }

  public void setDefault() {
    // default value is current date unless date has been overridden for
    // tracefile verification purposes.
    if(defaultDateOverride != null) {
      this.value = defaultDateOverride;
    } else {
      this.value = new SimpleDateFormat("yyyy-MM-dd")
          .format(Calendar.getInstance().getTime());
    }
  }

  public boolean isBlank() {
    throw new OPSVMachRuntimeException("isBlank() called on PTDate; need "
        + "to determine what PT considers a blank date.");
  }

  public PTBoolean isEqual(PTPrimitiveType op) {
    throw new OPSDataTypeException("isEqual not implemented for " +
      "Date.");
  }

  public PTBoolean isGreaterThan(PTPrimitiveType op) {
    throw new OPSDataTypeException("isGreaterThan not implemented for " +
      "Date.");
  }

  public PTBoolean isGreaterThanOrEqual(PTPrimitiveType op) {
    if(!(op instanceof PTDate)) {
      throw new OPSDataTypeException("Expected op to be PTDate.");
    }
    if(this.value.compareTo(((PTDate)op).read()) >= 0) {
      return new PTBoolean(true);
    }
    return new PTBoolean(false);
  }

  public PTBoolean isLessThan(PTPrimitiveType op) {
    throw new OPSDataTypeException("isLessThan is not supported for " +
      "Date.");
  }

  public PTBoolean isLessThanOrEqual(PTPrimitiveType op) {
    if(!(op instanceof PTDate)) {
      throw new OPSDataTypeException("Expected op to be PTDate.");
    }
    if(this.value.compareTo(((PTDate)op).read()) <= 0) {
      return new PTBoolean(true);
    }
    return new PTBoolean(false);
  }

  public boolean equals(Object obj) {
    if(obj == this)
      return true;
    if(obj == null)
      return false;
    if(!(obj instanceof PTDate))
      return false;
    PTDate other = (PTDate)obj;
    if(this.read().equals(other.read())) {
      return true;
    }
    return false;
  }
}
