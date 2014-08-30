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

/**
 * Implementation of the PeopleTools string data type.
 */
public final class PTString extends PTPrimitiveType<String> {

  private static Type staticTypeFlag = Type.STRING;
  private String s;

  /**
   * Constructs a new instance of the string data type;
   * can only be called by internal methods.
   */
  public PTString(final PTTypeConstraint origTc) {
    super(staticTypeFlag, origTc);
  }

  @Override
  public String read() {
    return this.s;
  }

  @Override
  public String readAsString() {
    return this.s;
  }

  @Override
  public void write(final String newValue) {
    this.checkIsWriteable();
    this.s = newValue;
  }

  @Override
  public void systemWrite(final String newValue) {
    this.s = newValue;
  }

  /**
   * Concatenates a string to this instance.
   * @param other the string to concatenate
   * @return a string object containing the concatenate
   *   value
   */
  public PTString concat(final PTString other) {
    return Environment.getFromLiteralPool(
      this.read().concat(other.read()));
  }

  @Override
  public void setDefault() {
    this.s = " ";
  }

  @Override
  public void copyValueFrom(final PTPrimitiveType src) {
    if (!(src instanceof PTString)) {
      throw new EntDataTypeException("Expected src to be PTString.");
    }
    this.write(((PTString) src).read());
  }

  @Override
  public PTPrimitiveType add(final PTPrimitiveType op) {
    throw new OPSVMachRuntimeException("add() not supported.");
  }

  @Override
  public PTPrimitiveType subtract(final PTPrimitiveType op) {
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

  @Override
  public PTBoolean isEqual(final PTPrimitiveType op) {
    if (!(op instanceof PTString)) {
      throw new EntDataTypeException("Expected op to be PTString.");
    }
    if (this.s.equals(((PTString) op).read())) {
      return Environment.TRUE;
    }
    return Environment.FALSE;
  }

  @Override
  public PTBoolean isGreaterThan(final PTPrimitiveType op) {
    if (!(op instanceof PTString)) {
      throw new EntDataTypeException("Expected op to be PTString.");
    }
    if (this.s.compareTo(((PTString) op).read()) > 0) {
      return Environment.TRUE;
    }
    return Environment.FALSE;
  }

  @Override
  public PTBoolean isGreaterThanOrEqual(final PTPrimitiveType op) {
    throw new EntDataTypeException("isGreaterThanOrEqual not "
        + "supported.");
  }

  @Override
  public PTBoolean isLessThan(final PTPrimitiveType op) {
    if (!(op instanceof PTString)) {
      throw new EntDataTypeException("Expected op to be PTString.");
    }
    if (this.s.compareTo(((PTString) op).read()) < 0) {
      return Environment.TRUE;
    }
    return Environment.FALSE;
  }

  @Override
  public PTBoolean isLessThanOrEqual(final PTPrimitiveType op) {
    throw new EntDataTypeException("isLessThanOrEqual not supported "
        + "for strings.");
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof PTString)) {
      return false;
    }

    final PTString other = (PTString) obj;
    if (this.read().equals(other.read())) {
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int HBC_INITIAL = 29, HBC_MULTIPLIER = 487;

    return new HashCodeBuilder(HBC_INITIAL,
        HBC_MULTIPLIER).append(this.read()).toHashCode();
  }

  @Override
  public boolean typeCheck(final PTType a) {
    return (a instanceof PTString
        && this.getType() == a.getType());
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",s=").append(this.s);
    return b.toString();
  }
}
