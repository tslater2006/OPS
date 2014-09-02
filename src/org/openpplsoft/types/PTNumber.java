/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.apache.logging.log4j.*;

import org.openpplsoft.runtime.*;

public final class PTNumber extends PTNumberType<BigDecimal> {

  private static Logger log = LogManager.getLogger(PTNumber.class.getName());

  private boolean isInteger;
  private BigDecimal bigDec;

  public PTNumber(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public BigDecimal read() {
    return this.bigDec;
  }

  public String readAsString() {
    return this.bigDec.toString();
  }

  public int readAsInteger() {
    /*
     * IMPORTANT NOTE: I'm using intValueExact here
     * because I want to see if any situations arise where
     * a Number can't be converted to an int exactly (intValueExact
     * throws ArithmeticEception if the BigDecimal object has a nonzero
     * fractional part or if it will overflow an int.
     */
    return this.bigDec.intValueExact();
  }

  public void write(final BigDecimal newValue) {
    this.checkIsWriteable();

    // NOTE: BigDecimal objects are immutable, so no need to instantate new
    // object here.
    this.bigDec = newValue;
  }

  public void systemWrite(BigDecimal newValue) {
    // NOTE: BigDecimal objects are immutable, so no need to instantate new
    // object here.
    this.bigDec = newValue;
  }

  public void setDefault() {
    this.bigDec = BigDecimal.ZERO;
  }

  public void copyValueFrom(PTPrimitiveType src) {
    if(src instanceof PTNumber) {
      this.write(((PTNumber) src).read());
    } else if(src instanceof PTInteger) {
      this.write(new BigDecimal(((PTInteger) src).read()));
    } else {
      throw new OPSDataTypeException("Expected src to be PTNumber or PTInteger.");
    }
  }

  @Override
  public PTNumberType add(PTNumberType op) {
    if(op instanceof PTInteger) {
         return Environment.getFromLiteralPool(
              this.bigDec.add(new BigDecimal(((PTInteger) op).read())));
    } else {
      throw new OPSDataTypeException("Expected PTInteger.");
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

  @Override
  public PTBoolean isEqual(PTPrimitiveType op) {
    if(op instanceof PTNumber) {
      if(this.bigDec.compareTo(((PTNumber) op).read()) == 0) {
        return Environment.TRUE;
      }
    } else if(op instanceof PTInteger) {
      if(this.bigDec.compareTo(new BigDecimal(((PTInteger) op).read())) == 0) {
        return Environment.TRUE;
      }
    } else {
      throw new OPSDataTypeException("Expected op to be PTNumber or PTInteger.");
    }

    return Environment.FALSE;
  }

  @Override
  public PTBoolean isGreaterThan(PTPrimitiveType op) {
    if(op instanceof PTNumber) {
      if(this.bigDec.compareTo(((PTNumber) op).read()) > 0) {
        return Environment.TRUE;
      }
    } else {
      throw new OPSDataTypeException("Expected op to be PTNumber.");
    }

    return Environment.FALSE;
  }

  @Override
  public PTBoolean isGreaterThanOrEqual(PTPrimitiveType op) {
    throw new OPSDataTypeException("isGreaterThanOrEqual not "
        + "supported.");
  }

  @Override
  public PTBoolean isLessThan(PTPrimitiveType op) {
    if(op instanceof PTNumber) {
      if(this.bigDec.compareTo(((PTNumber) op).read()) < 0) {
        return Environment.TRUE;
      }
    } else if(op instanceof PTInteger) {
      if(this.bigDec.compareTo(new BigDecimal(((PTInteger) op).read())) < 0) {
        return Environment.TRUE;
      }
    } else {
      throw new OPSDataTypeException("Expected op to be PTNumber "+
          "or PTInteger.");
    }
    return Environment.FALSE;
  }

  @Override
  public PTBoolean isLessThanOrEqual(PTPrimitiveType op) {
    if(op instanceof PTNumber) {
      if(this.bigDec.compareTo(((PTNumber) op).read()) <= 0) {
        return Environment.TRUE;
      }
    } else if(op instanceof PTInteger) {
      if(this.bigDec.compareTo(new BigDecimal(((PTInteger) op).read())) <= 0) {
        return Environment.TRUE;
      }
    } else {
      throw new OPSDataTypeException("Expected op to be PTNumber "+
          "or PTInteger.");
    }
    return Environment.FALSE;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == this)
      return true;
    if(obj == null)
      return false;
    if(!(obj instanceof PTNumber))
      return false;

    /*
     * Do NOT forget that equals() will not do here;
     * BigDecimal objects must be compared via compareTo().
     */
    PTNumber other = (PTNumber)obj;
    if(this.bigDec.compareTo(other.read()) == 0) {
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int HBC_INITIAL = 563, HBC_MULTIPLIER = 281;

    return new HashCodeBuilder(HBC_INITIAL,
        HBC_MULTIPLIER).append(this.bigDec).toHashCode();
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder(super.toString());
    b.append(",bigDec=").append(this.bigDec);
    return b.toString();
  }
}
