/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.EnumSet;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.openpplsoft.runtime.*;

public final class PTBoolean extends PTPrimitiveType<Boolean> {

  private Boolean b;

  public PTBoolean(PTTypeConstraint origTc) {
    super(origTc);
  }

  public Boolean read() {
    return this.b;
  }

  public String readAsString() {
    return this.b.toString();
  }

  public void write(Boolean newValue) {
    this.checkIsWriteable();
    this.b = newValue;
  }

  public void systemWrite(Boolean newValue) {
    this.b = newValue;
  }

  public void setDefault() {
    throw new OPSDataTypeException("setDefault not implemented.");
  }

  public void copyValueFrom(PTPrimitiveType src) {
    if(!(src instanceof PTBoolean)) {
      throw new OPSDataTypeException("Expected src to be PTBoolean.");
    }
    this.write(((PTBoolean)src).read());
  }

  public PTBoolean isEqual(PTPrimitiveType op) {
    if(!(op instanceof PTBoolean)) {
      throw new OPSDataTypeException("Expected op to be PTBoolean.");
    }
    if(this.b.equals(((PTBoolean)op).read())) {
      return Environment.TRUE;
    }
    return Environment.FALSE;
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

  public boolean equals(Object obj) {
    if(obj == this)
      return true;
    if(obj == null)
      return false;
    if(!(obj instanceof PTBoolean))
      return false;

    PTBoolean other = (PTBoolean)obj;
    if(this.read().equals(other.read())) {
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 41, HCB_MULTIPLIER = 337;

    return new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.read()).toHashCode();
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder(super.toString());
    b.append(",b=").append(this.b);
    return b.toString();
  }
}

