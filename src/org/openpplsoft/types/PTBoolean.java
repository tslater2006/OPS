/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.runtime.*;

public final class PTBoolean extends PTPrimitiveType<Boolean> {

  private static PTTypeConstraint<PTBoolean> boolTc;

  static {
    boolTc = new PTTypeConstraint<PTBoolean>(PTBoolean.class);
  }

  public PTBoolean(final Boolean initialVal) {
    super(boolTc);
    if (initialVal == null) {
      throw new OPSVMachRuntimeException("Failed to initialize PTBoolean; "
          + "initial value is null.");
    }
    this.value = initialVal;
  }

  public PTBoolean negationOf() {
    return new PTBoolean(!this.value);
  }

  public PTBoolean(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public static PTTypeConstraint<PTBoolean> getTc() {
    return boolTc;
  }

  public void setBlank() {
    this.value = false;
  }

  public boolean isBlank() {
    throw new OPSVMachRuntimeException("isBlank() called on PTBoolean; need "
        + "to determine if false really does constitute a blank value in PT.");
  }

  @Override
  protected Boolean primitiveToRaw(final PTPrimitiveType src) {
    if(!(src instanceof PTBoolean)) {
      throw new OPSDataTypeException("Expected src to be PTBoolean.");
    }
    return ((PTBoolean)src).read();
  }

  public PTBoolean isEqual(PTPrimitiveType op) {
    if(!(op instanceof PTBoolean)) {
      throw new OPSDataTypeException("Expected op to be PTBoolean.");
    }
    if(this.value.equals(((PTBoolean)op).read())) {
      return new PTBoolean(true);
    }
    return new PTBoolean(false);
  }

  public PTBoolean isGreaterThanOrEqual(PTPrimitiveType op) {
    throw new OPSDataTypeException("isGreaterThanOrEqual not "
      + "supported.");
  }

  public PTBoolean isGreaterThan(PTPrimitiveType op) {
    throw new OPSDataTypeException("isGreaterThan not "
      + "supported.");
  }

  public PTBoolean isLessThan(PTPrimitiveType op) {
    throw new OPSDataTypeException("isLessThan is not supported for " +
      "booleans.");
  }

  public PTBoolean isLessThanOrEqual(PTPrimitiveType op) {
    throw new OPSDataTypeException("isLessThanOrEqual is not supported for " +
      "booleans.");
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == this)
      return true;
    if(obj == null)
      return false;
    if(!(obj instanceof PTBoolean))
      return false;

    PTBoolean other = (PTBoolean) obj;
    if(this.read().equals(other.read())) {
      return true;
    }
    return false;
  }
}

