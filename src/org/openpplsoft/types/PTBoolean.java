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

  private static Type staticTypeFlag = Type.BOOLEAN;
  private Boolean b;

  private PTBoolean() {
    super(staticTypeFlag);
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
    this.checkIsSystemWriteable();
    this.b = newValue;
  }

  public void setDefault() {
    throw new EntDataTypeException("setDefault not implemented.");
  }

  public void copyValueFrom(PTPrimitiveType src) {
    if(!(src instanceof PTBoolean)) {
      throw new EntDataTypeException("Expected src to be PTBoolean.");
    }
    this.write(((PTBoolean)src).read());
  }

  public PTPrimitiveType add(PTPrimitiveType op) {
    throw new OPSVMachRuntimeException("add() not supported.");
  }

  public PTPrimitiveType subtract(PTPrimitiveType op) {
    throw new OPSVMachRuntimeException("subtract() not supported.");
  }

  public PTPrimitiveType mul(PTPrimitiveType op) {
    throw new OPSVMachRuntimeException("mul() not supported.");
  }

  public PTPrimitiveType div(PTPrimitiveType op) {
    throw new OPSVMachRuntimeException("div() not supported.");
  }

  public PTBoolean isEqual(PTPrimitiveType op) {
    if(!(op instanceof PTBoolean)) {
      throw new EntDataTypeException("Expected op to be PTBoolean.");
    }
    if(this.b.equals(((PTBoolean)op).read())) {
      return Environment.TRUE;
    }
    return Environment.FALSE;
  }

  public PTBoolean isGreaterThanOrEqual(PTPrimitiveType op) {
    throw new EntDataTypeException("isGreaterThanOrEqual not "
      + "supported.");
  }

  public PTBoolean isGreaterThan(PTPrimitiveType op) {
    throw new EntDataTypeException("isGreaterThan not "
      + "supported.");
  }

  public PTBoolean isLessThan(PTPrimitiveType op) {
    throw new EntDataTypeException("isLessThan is not supported for " +
      "booleans.");
  }

  public PTBoolean isLessThanOrEqual(PTPrimitiveType op) {
    throw new EntDataTypeException("isLessThanOrEqual is not supported for " +
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
    final int HBC_INITIAL = 41, HBC_MULTIPLIER = 337;

    return new HashCodeBuilder(HBC_INITIAL,
        HBC_MULTIPLIER).append(this.read()).toHashCode();
  }

  public boolean typeCheck(PTType a) {
    return (a instanceof PTBoolean &&
      this.getType() == a.getType());
  }

  public static PTBoolean getSentinel() {
    // If the sentinel has already been cached, return it immediately.
    String cacheKey = getCacheKey();
    if(PTType.isSentinelCached(cacheKey)) {
      return (PTBoolean)PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    PTBoolean sentinelObj = new PTBoolean();
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  public PTPrimitiveType alloc() {
    PTBoolean newObj = new PTBoolean();
    PTType.clone(this, newObj);
    return newObj;
  }

  private static String getCacheKey() {
    StringBuilder b = new StringBuilder(staticTypeFlag.name());
    return b.toString();
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder(super.toString());
    b.append(",b=").append(this.b);
    return b.toString();
  }
}

