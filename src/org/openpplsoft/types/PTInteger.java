/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.EnumSet;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.apache.logging.log4j.*;

import org.openpplsoft.runtime.*;

public final class PTInteger extends PTPrimitiveType<Integer> {

  private static Logger log = LogManager.getLogger(PTInteger.class.getName());
  private static Type staticTypeFlag = Type.INTEGER;
  private Integer i;

  private PTInteger() {
    super(staticTypeFlag);
  }

  public Integer read() {
    return this.i;
  }

  public String readAsString() {
    return this.i.toString();
  }

  public void write(Integer newValue) {
    this.checkIsWriteable();
    this.i = newValue;
  }

  public void systemWrite(Integer newValue) {
    this.checkIsSystemWriteable();
    this.i = newValue;
  }

  public void setDefault() {
    throw new EntDataTypeException("setDefault not implemented.");
  }

  public void copyValueFrom(PTPrimitiveType src) {
    if(src instanceof PTInteger) {
      this.write(((PTInteger)src).read());
    } else if (src instanceof PTNumber) {
      this.write(((PTNumber)src).readAsInteger());
    } else {
      throw new EntDataTypeException("Expected src to be PTInteger.");
    }
  }

  public PTPrimitiveType add(PTPrimitiveType op) {
    if(!(op instanceof PTInteger)) {
      throw new EntDataTypeException("Expected op to be PTInteger.");
    }
    return Environment.getFromLiteralPool(
      this.read() + ((PTInteger)op).read());
  }

  public PTPrimitiveType subtract(PTPrimitiveType op) {
    if(!(op instanceof PTInteger)) {
      throw new EntDataTypeException("Expected op to be PTInteger.");
    }
    return Environment.getFromLiteralPool(
      this.read() - ((PTInteger)op).read());
  }

  @Override
  public PTPrimitiveType mul(PTPrimitiveType op) {
    throw new OPSVMachRuntimeException("mul() not supported.");
  }

  @Override
  public PTPrimitiveType div(PTPrimitiveType op) {
    if(!(op instanceof PTInteger)) {
      throw new EntDataTypeException("Expected op to be PTInteger.");
    }
    return Environment.getFromLiteralPool(
      this.read() / ((PTInteger)op).read());
  }

  public boolean equals(Object obj) {
    if(obj == this)
      return true;
    if(obj == null)
      return false;
    if(!(obj instanceof PTInteger))
      return false;

    PTInteger other = (PTInteger)obj;
    if(this.read().equals(other.read())) {
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int HBC_INITIAL = 359, HBC_MULTIPLIER = 7;

    return new HashCodeBuilder(HBC_INITIAL,
        HBC_MULTIPLIER).append(this.read()).toHashCode();
  }

  public PTBoolean isEqual(PTPrimitiveType op) {
    if(!(op instanceof PTInteger)) {
      throw new EntDataTypeException("Expected op to be PTInteger.");
    }
    if(this.i.compareTo(((PTInteger)op).read()) == 0) {
      return Environment.TRUE;
    }
    return Environment.FALSE;
  }

  public PTBoolean isGreaterThan(PTPrimitiveType op) {
    if(!(op instanceof PTInteger)) {
      throw new EntDataTypeException("Expected op to be PTInteger.");
    }
    if(this.i.compareTo(((PTInteger)op).read()) > 0) {
      return Environment.TRUE;
    }
    return Environment.FALSE;
  }

  public PTBoolean isGreaterThanOrEqual(PTPrimitiveType op) {
    throw new EntDataTypeException("isGreaterThanOrEqual not "
        + "supported.");
  }

  public PTBoolean isLessThan(PTPrimitiveType op) {
    if(!(op instanceof PTInteger)) {
      throw new EntDataTypeException("Expected op to be PTInteger.");
    }
    if(this.i.compareTo(((PTInteger)op).read()) < 0) {
      return Environment.TRUE;
    }
    return Environment.FALSE;
  }

  public PTBoolean isLessThanOrEqual(PTPrimitiveType op) {
    if(!(op instanceof PTInteger)) {
      throw new EntDataTypeException("Expected op to be PTInteger.");
    }
    if(this.i.compareTo(((PTInteger)op).read()) <= 0) {
      return Environment.TRUE;
    }
    return Environment.FALSE;
  }

  public boolean typeCheck(PTType a) {
    return (a instanceof PTInteger &&
        this.getType() == a.getType());
  }

  public static PTInteger getSentinel() {

    // If the sentinel has already been cached, return it immediately.
    String cacheKey = getCacheKey();
    if(PTType.isSentinelCached(cacheKey)) {
      return (PTInteger)PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    PTInteger sentinelObj = new PTInteger();
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  public PTPrimitiveType alloc() {
    PTInteger newObj = new PTInteger();
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
    b.append(",i=").append(this.i);
    return b.toString();
  }
}
