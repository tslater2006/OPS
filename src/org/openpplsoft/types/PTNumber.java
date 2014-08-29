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

public final class PTNumber extends PTPrimitiveType<Double> {

  private static Logger log = LogManager.getLogger(PTNumber.class.getName());
  private static Type staticTypeFlag = Type.NUMBER;

  private boolean isInteger;
  private Double d;

  private PTNumber() {
    super(staticTypeFlag);
  }

  public Double read() {
    return this.d;
  }

  public String readAsString() {
    if(this.isInteger) {
      return Integer.toString(this.d.intValue());
    }
    return d.toString();
  }

  /**
   * TODO(mquinn): This could cause issues due to loss of precision;
   * although this maybe intentional in alot of cases (i.e., 1.0 to 1
   * in loop counters), it could be the cause of future problems.
   */
  public int readAsInteger() {
    return this.d.intValue();
  }

  public void write(int newValue) {
    this.checkIsWriteable();
    this.isInteger = true;
    this.d = new Double(newValue);
  }

  public void write(Double newValue) {
    this.checkIsWriteable();
    this.isInteger = false;
    this.d = newValue;
  }

  public void systemWrite(Double newValue) {
    this.checkIsSystemWriteable();
    this.isInteger = false;
    this.d = newValue;
  }

  public void setDefault() {
    this.d = 0.0;
  }

  public void copyValueFrom(PTPrimitiveType src) {
    if(src instanceof PTNumber) {
      this.write(((PTNumber)src).read());
    } else if(src instanceof PTInteger) {
      this.write(new Double(((PTInteger)src).read()));
    } else {
      throw new EntDataTypeException("Expected src to be PTNumber.");
    }
  }

  public PTPrimitiveType add(PTPrimitiveType op) {
    if(op instanceof PTInteger) {
         return Environment.getFromLiteralPool(
              this.read() + new Double(((PTInteger)op).read()));
    } else {
      throw new EntDataTypeException("Unexpected op type "+
        "provided to add().");
    }
  }

  public PTPrimitiveType subtract(PTPrimitiveType op) {
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

  public PTBoolean isEqual(PTPrimitiveType op) {
    throw new EntDataTypeException("isEqual is not implemented for " +
        "numbers.");
  }

  public PTBoolean isGreaterThan(PTPrimitiveType op) {
    if(!(op instanceof PTNumber)) {
      throw new EntDataTypeException("Expected op to be PTNumber.");
    }
    if(this.d.compareTo(((PTNumber)op).read()) > 0) {
      return Environment.TRUE;
    }
    return Environment.FALSE;
  }

  public PTBoolean isGreaterThanOrEqual(PTPrimitiveType op) {
    throw new EntDataTypeException("isGreaterThanOrEqual not "
        + "supported.");
  }

  public PTBoolean isLessThan(PTPrimitiveType op) {
    if(op instanceof PTNumber) {
      if(this.d.compareTo(((PTNumber)op).read()) < 0) {
        return Environment.TRUE;
      }
    } else if(op instanceof PTInteger) {
      if(this.d.compareTo(new Double(((PTInteger)op).read())) < 0) {
        return Environment.TRUE;
      }
    } else {
      throw new EntDataTypeException("Expected op to be PTNumber "+
          "or PTInteger.");
    }
    return Environment.FALSE;
  }

  public PTBoolean isLessThanOrEqual(PTPrimitiveType op) {
    if(op instanceof PTNumber) {
      if(this.d.compareTo(((PTNumber)op).read()) <= 0) {
        return Environment.TRUE;
      }
    } else if(op instanceof PTInteger) {
        if(this.d.compareTo(new Double(((PTInteger)op).read())) <= 0) {
          return Environment.TRUE;
        }
    } else {
      throw new EntDataTypeException("Expected op to be PTNumber "+
          "or PTInteger.");
    }
    return Environment.FALSE;
  }

  public boolean equals(Object obj) {
    if(obj == this)
      return true;
    if(obj == null)
      return false;
    if(!(obj instanceof PTNumber))
      return false;

    PTNumber other = (PTNumber)obj;
    if(this.read().equals(other.read())) {
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int HBC_INITIAL = 563, HBC_MULTIPLIER = 281;

    return new HashCodeBuilder(HBC_INITIAL,
        HBC_MULTIPLIER).append(this.read()).toHashCode();
  }

  public PTPrimitiveType castTo(PTPrimitiveType t) {
    throw new EntDataTypeException("castTo() has not been implemented.");
  }

  public boolean typeCheck(PTType a) {
    return (a instanceof PTNumber &&
        this.getType() == a.getType());
  }

  public static PTNumber getSentinel() {

    // If the sentinel has already been cached, return it immediately.
    String cacheKey = getCacheKey();
    if(PTType.isSentinelCached(cacheKey)) {
      return (PTNumber)PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    PTNumber sentinelObj = new PTNumber();
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  public PTPrimitiveType alloc() {
    PTNumber newObj = new PTNumber();
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
    if(this.isInteger) {
      b.append(",d(int)=").append(this.d.intValue());
    } else {
      b.append(",d=").append(this.d);
    }
    return b.toString();
  }
}
