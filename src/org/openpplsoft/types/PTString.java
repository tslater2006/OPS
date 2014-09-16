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

  private static PTTypeConstraint<PTString> stringTc;

  static {
    stringTc = new PTTypeConstraint<PTString>(PTString.class);
  }

  private String s;

  public PTString(final String initialVal) {
    super(stringTc);
    this.s = initialVal;
  }

  public PTString(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public static PTTypeConstraint<PTString> getTc() {
    return stringTc;
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
    return new PTString(this.read().concat(other.read()));
  }

  @Override
  public void setDefault() {
    this.s = " ";
  }

  @Override
  public void copyValueFrom(final PTPrimitiveType src) {
    if (!(src instanceof PTString)) {
      throw new OPSDataTypeException("Expected src to be PTString.");
    }
    this.write(((PTString) src).read());
  }

  @Override
  public PTBoolean isEqual(final PTPrimitiveType op) {
    if (!(op instanceof PTString)) {
      throw new OPSDataTypeException("Expected op to be PTString.");
    }
    if (this.s.equals(((PTString) op).read())) {
      return new PTBoolean(true);
    }
    return new PTBoolean(false);
  }

  @Override
  public PTBoolean isGreaterThan(final PTPrimitiveType op) {
    if (!(op instanceof PTString)) {
      throw new OPSDataTypeException("Expected op to be PTString.");
    }
    if (this.s.compareTo(((PTString) op).read()) > 0) {
      return new PTBoolean(true);
    }
    return new PTBoolean(false);
  }

  @Override
  public PTBoolean isGreaterThanOrEqual(final PTPrimitiveType op) {
    throw new OPSDataTypeException("isGreaterThanOrEqual not "
        + "supported.");
  }

  @Override
  public PTBoolean isLessThan(final PTPrimitiveType op) {
    if (!(op instanceof PTString)) {
      throw new OPSDataTypeException("Expected op to be PTString.");
    }
    if (this.s.compareTo(((PTString) op).read()) < 0) {
      return new PTBoolean(true);
    }
    return new PTBoolean(false);
  }

  @Override
  public PTBoolean isLessThanOrEqual(final PTPrimitiveType op) {
    throw new OPSDataTypeException("isLessThanOrEqual not supported "
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
    final int HCB_INITIAL = 29, HCB_MULTIPLIER = 487;

    return new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.read()).toHashCode();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",s=").append(this.s);
    return b.toString();
  }
}
