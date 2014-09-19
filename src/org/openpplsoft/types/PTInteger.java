/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.logging.log4j.*;

import org.openpplsoft.runtime.*;

public final class PTInteger extends PTNumberType<Integer> {

  private static Logger log = LogManager.getLogger(PTInteger.class.getName());
  private static PTTypeConstraint<PTInteger> intTc;

  static {
    intTc = new PTTypeConstraint<PTInteger>(PTInteger.class);
  }

  public PTInteger(final Integer initialVal) {
    super(intTc);
    this.value = initialVal;
  }

  public PTInteger(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public static PTTypeConstraint<PTInteger> getTc() {
    return intTc;
  }

  public void setDefault() {
    this.value = 0;
  }

  public boolean isBlank() {
    return (this.value == 0);
  }

  public void copyValueFrom(final PTPrimitiveType src) {
    if(src instanceof PTInteger) {
      this.write(((PTInteger) src).read());
    } else if (src instanceof PTNumber) {
      this.write(((PTNumber) src).readAsInteger());
    } else {
      throw new OPSDataTypeException("Expected src to be PTInteger or PTNumber.");
    }
  }

  @Override
  public PTNumberType add(PTNumberType op) {
    if(!(op instanceof PTInteger)) {
      throw new OPSDataTypeException("Expected op to be PTInteger.");
    }
    return new PTInteger(this.read() + ((PTInteger) op).read());
  }

  @Override
  public PTNumberType sub(PTNumberType op) {
    if(!(op instanceof PTInteger)) {
      throw new OPSDataTypeException("Expected op to be PTInteger.");
    }
    return new PTInteger(this.read() - ((PTInteger)op).read());
  }

  @Override
  public PTNumberType mul(PTNumberType op) {
    throw new OPSVMachRuntimeException("mul() not supported.");
  }

  /**
   * CRITICAL NOTE: PeopleTools *NEVER* uses integer division, even if both
   * are operands are integers. Floating decimal point division is always used. See
   * http://docs.oracle.com/cd/E38689_01/pt853pbr0/eng/pt/tpcd/concept_DataTypes-074b53.html
   * for associated documentation.
   */
  @Override
  public PTNumberType div(PTNumberType op) {
    if(!(op instanceof PTInteger)) {
      throw new OPSDataTypeException("Expected op to be PTInteger.");
    }

    /*
     * I am passing 32 as the scale to the divide method because the PeopleTools
     * documentation (see link above in Javadoc for this method) says that the
     * Number data type allows for a max of 32 digits to the right of the decimal.
     * I am passing HALF_EVEN as the rounding mode b/c that's what Java uses for
     * decimal and float arithmetic. These arguments are required, otherwise an
     * ArithmeticException will be thrown the first time a division occurs that
     * does not result in a quotient with a terminating expansion.
     */
    BigDecimal quotient =
        new BigDecimal(this.read()).divide(
            new BigDecimal(((PTInteger) op).read()), 32, RoundingMode.HALF_EVEN);
    log.debug("Divided {} by {} to get {}.", this.read(), ((PTInteger) op).read(),
        quotient);

    return new PTNumber(quotient);
  }

  public PTBoolean isEqual(PTPrimitiveType op) {
    if(!(op instanceof PTInteger)) {
      throw new OPSDataTypeException("Expected op to be PTInteger.");
    }
    if(this.value.compareTo(((PTInteger)op).read()) == 0) {
      return new PTBoolean(true);
    }
    return new PTBoolean(false);
  }

  public PTBoolean isGreaterThan(PTPrimitiveType op) {
    if(!(op instanceof PTInteger)) {
      throw new OPSDataTypeException("Expected op to be PTInteger.");
    }
    if(this.value.compareTo(((PTInteger)op).read()) > 0) {
      return new PTBoolean(true);
    }
    return new PTBoolean(false);
  }

  public PTBoolean isGreaterThanOrEqual(PTPrimitiveType op) {
    throw new OPSDataTypeException("isGreaterThanOrEqual not "
        + "supported.");
  }

  public PTBoolean isLessThan(PTPrimitiveType op) {
    if(!(op instanceof PTInteger)) {
      throw new OPSDataTypeException("Expected op to be PTInteger.");
    }
    if(this.value.compareTo(((PTInteger)op).read()) < 0) {
      return new PTBoolean(true);
    }
    return new PTBoolean(false);
  }

  public PTBoolean isLessThanOrEqual(PTPrimitiveType op) {
    if(!(op instanceof PTInteger)) {
      throw new OPSDataTypeException("Expected op to be PTInteger.");
    }
    if(this.value.compareTo(((PTInteger)op).read()) <= 0) {
      return new PTBoolean(true);
    }
    return new PTBoolean(false);
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
}
