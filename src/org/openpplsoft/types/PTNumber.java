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

public final class PTNumber extends PTNumberType<Double> {

  private static Logger log = LogManager.getLogger(PTNumber.class.getName());

  private boolean isInteger;
  private Double d;

  public PTNumber(PTTypeConstraint origTc) {
    super(origTc);
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
      throw new OPSDataTypeException("Expected src to be PTNumber.");
    }
  }

  @Override
  public PTNumberType add(PTNumberType op) {
    if(op instanceof PTInteger) {
         return Environment.getFromLiteralPool(
              this.read() + new Double(((PTInteger)op).read()));
    } else {
      throw new OPSDataTypeException("Unexpected op type "+
        "provided to add().");
    }
  }

  @Override
  public PTNumberType sub(PTNumberType op) {
    throw new OPSVMachRuntimeException("sub() not supported.");
  }

  @Override
  public PTNumberType mul(PTNumberType op) {
    throw new OPSVMachRuntimeException("mul() not supported.");
  }

  @Override
  public PTNumberType div(PTNumberType op) {
    throw new OPSVMachRuntimeException("div() not supported.");
  }

  public PTBoolean isEqual(PTPrimitiveType op) {
    throw new OPSDataTypeException("isEqual is not implemented for " +
        "numbers.");
  }

  public PTBoolean isGreaterThan(PTPrimitiveType op) {
    if(!(op instanceof PTNumber)) {
      throw new OPSDataTypeException("Expected op to be PTNumber.");
    }
    if(this.d.compareTo(((PTNumber)op).read()) > 0) {
      return Environment.TRUE;
    }
    return Environment.FALSE;
  }

  public PTBoolean isGreaterThanOrEqual(PTPrimitiveType op) {
    throw new OPSDataTypeException("isGreaterThanOrEqual not "
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
      throw new OPSDataTypeException("Expected op to be PTNumber "+
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
      throw new OPSDataTypeException("Expected op to be PTNumber "+
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
