/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.runtime.*;

/**
 * Implementation of the PeopleTools string data type.
 */
public class PTString extends PTPrimitiveType<String> {

  private static PTTypeConstraint<PTString> stringTc;

  static {
    stringTc = new PTTypeConstraint<PTString>(PTString.class);
  }

  public PTString(final String initialVal) {
    super(stringTc);

    if (initialVal == null) {
      throw new OPSVMachRuntimeException("Failed to initialize new PTString; "
          + "initial value is null.");
    }

    this.value = initialVal;
  }

  public PTString(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public static PTTypeConstraint<PTString> getTc() {
    return stringTc;
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
  public void setBlank() {
    this.value = "";
  }

  @Override
  public boolean isBlank() {
    return this.value.trim().length() == 0;
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
    if (this.value.equals(((PTString) op).read())) {
      return new PTBoolean(true);
    }
    return new PTBoolean(false);
  }

  @Override
  public PTBoolean isGreaterThan(final PTPrimitiveType op) {
    if (!(op instanceof PTString)) {
      throw new OPSDataTypeException("Expected op to be PTString.");
    }
    if (this.value.compareTo(((PTString) op).read()) > 0) {
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
    if (this.value.compareTo(((PTString) op).read()) < 0) {
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
}
