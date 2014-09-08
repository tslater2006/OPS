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

  private static String defaultDateOverride;
  private String d;

  public PTDate(final PTTypeConstraint origTc) {
    super(origTc);

    // default value is current date unless date has been overridden for
    // tracefile verification purposes.
    if(defaultDateOverride != null) {
      this.d = defaultDateOverride;
    } else {
      d = new SimpleDateFormat("yyyy-MM-dd")
            .format(Calendar.getInstance().getTime());
    }
  }

  public static void overrideDefaultDate(final String d) {
    defaultDateOverride = d;
  }

  public String read() {
    return this.d;
  }

  public String readAsString() {
    return this.d;
  }

  public void write(String newValue) {
    this.checkIsWriteable();
    this.d = newValue;
  }

  public void systemWrite(String newValue) {
    this.d = newValue;
  }

  public void copyValueFrom(PTPrimitiveType src) {
    if (!(src instanceof PTDate)) {
      throw new OPSDataTypeException("Expected src to be PTDate.");
    }
    this.write(((PTDate) src).read());
  }

  public void setDefault() {
    this.d = null;
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
    if(this.d.compareTo(((PTDate)op).read()) >= 0) {
      return Environment.TRUE;
    }
    return Environment.FALSE;
  }

  public PTBoolean isLessThan(PTPrimitiveType op) {
    throw new OPSDataTypeException("isLessThan is not supported for " +
      "Date.");
  }

  public PTBoolean isLessThanOrEqual(PTPrimitiveType op) {
    if(!(op instanceof PTDate)) {
      throw new OPSDataTypeException("Expected op to be PTDate.");
    }
    if(this.d.compareTo(((PTDate)op).read()) <= 0) {
      return Environment.TRUE;
    }
    return Environment.FALSE;
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

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 71, HCB_MULTIPLIER = 967;

    return new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.read()).toHashCode();
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder(super.toString());
    b.append(",d=").append(this.d);
    return b.toString();
  }
}
