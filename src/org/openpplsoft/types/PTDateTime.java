/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.text.SimpleDateFormat;

import java.util.*;

import org.openpplsoft.runtime.*;

public final class PTDateTime extends PTPrimitiveType<String> {

  private static PTTypeConstraint<PTDateTime> dateTimeTc;

  static {
    dateTimeTc = new PTTypeConstraint<PTDateTime>(PTDateTime.class);
  }

  public PTDateTime(final PTTypeConstraint origTc) {
    super(origTc);

    // default value is current date and time.
    this.value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        .format(Calendar.getInstance().getTime());
  }

  public static PTTypeConstraint<PTDateTime> getTc() {
    return dateTimeTc;
  }

  public void copyValueFrom(PTPrimitiveType src) {
    throw new OPSDataTypeException("copyValueFrom is not yet supported.");
  }

  public void setDefault() {
    this.value = null;
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
    if(!(op instanceof PTDateTime)) {
      throw new OPSDataTypeException("Expected op to be PTDateTime.");
    }
    if(this.value.compareTo(((PTDateTime)op).read()) >= 0) {
      return new PTBoolean(true);
    }
    return new PTBoolean(false);
  }

  public PTBoolean isLessThan(PTPrimitiveType op) {
    throw new OPSDataTypeException("isLessThan is not supported for " +
      "Date.");
  }

  public PTBoolean isLessThanOrEqual(PTPrimitiveType op) {
    if(!(op instanceof PTDateTime)) {
      throw new OPSDataTypeException("Expected op to be PTDateTime.");
    }
    if(this.value.compareTo(((PTDateTime)op).read()) <= 0) {
      return new PTBoolean(true);
    }
    return new PTBoolean(false);
  }

  public boolean equals(Object obj) {
    if(obj == this)
      return true;
    if(obj == null)
      return false;
    if(!(obj instanceof PTDateTime))
      return false;

    PTDateTime other = (PTDateTime)obj;
    if(this.read().equals(other.read())) {
      return true;
    }
    return false;
  }
}
