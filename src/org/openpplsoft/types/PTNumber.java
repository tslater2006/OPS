/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.math.BigDecimal;

import org.apache.logging.log4j.*;

import org.openpplsoft.runtime.*;

public final class PTNumber extends PTNumberType<BigDecimal> {

  private static Logger log = LogManager.getLogger(PTNumber.class.getName());
  private static PTTypeConstraint<PTNumber> numTc;

  static {
    numTc = new PTTypeConstraint<PTNumber>(PTNumber.class);
  }

  public PTNumber(final BigDecimal initialVal) {
    super(numTc);
    this.value = initialVal;
  }

  public PTNumber(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public static PTTypeConstraint<PTNumber> getTc() {
    return numTc;
  }

  public int readAsInteger() {
    /*
     * IMPORTANT NOTE: I'm using intValueExact here
     * because I want to see if any situations arise where
     * a Number can't be converted to an int exactly (intValueExact
     * throws ArithmeticEception if the BigDecimal object has a nonzero
     * fractional part or if it will overflow an int.
     */
    return this.value.intValueExact();
  }

  public void setDefault() {
    this.value = BigDecimal.ZERO;
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
         return new PTNumber(
              this.value.add(new BigDecimal(((PTInteger) op).read())));
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
      if(this.value.compareTo(((PTNumber) op).read()) == 0) {
        return new PTBoolean(true);
      }
    } else if(op instanceof PTInteger) {
      if(this.value.compareTo(new BigDecimal(((PTInteger) op).read())) == 0) {
        return new PTBoolean(true);
      }
    } else {
      throw new OPSDataTypeException("Expected op to be PTNumber or PTInteger.");
    }

    return new PTBoolean(false);
  }

  @Override
  public PTBoolean isGreaterThan(PTPrimitiveType op) {
    if(op instanceof PTNumber) {
      if(this.value.compareTo(((PTNumber) op).read()) > 0) {
        return new PTBoolean(true);
      }
    } else {
      throw new OPSDataTypeException("Expected op to be PTNumber.");
    }

    return new PTBoolean(false);
  }

  @Override
  public PTBoolean isGreaterThanOrEqual(PTPrimitiveType op) {
    throw new OPSDataTypeException("isGreaterThanOrEqual not "
        + "supported.");
  }

  @Override
  public PTBoolean isLessThan(PTPrimitiveType op) {
    if(op instanceof PTNumber) {
      if(this.value.compareTo(((PTNumber) op).read()) < 0) {
        return new PTBoolean(true);
      }
    } else if(op instanceof PTInteger) {
      if(this.value.compareTo(new BigDecimal(((PTInteger) op).read())) < 0) {
        return new PTBoolean(true);
      }
    } else {
      throw new OPSDataTypeException("Expected op to be PTNumber "+
          "or PTInteger.");
    }
    return new PTBoolean(false);
  }

  @Override
  public PTBoolean isLessThanOrEqual(PTPrimitiveType op) {
    if(op instanceof PTNumber) {
      if(this.value.compareTo(((PTNumber) op).read()) <= 0) {
        return new PTBoolean(true);
      }
    } else if(op instanceof PTInteger) {
      if(this.value.compareTo(new BigDecimal(((PTInteger) op).read())) <= 0) {
        return new PTBoolean(true);
      }
    } else {
      throw new OPSDataTypeException("Expected op to be PTNumber "+
          "or PTInteger.");
    }
    return new PTBoolean(false);
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
    if(this.value.compareTo(other.read()) == 0) {
      return true;
    }
    return false;
  }
}
