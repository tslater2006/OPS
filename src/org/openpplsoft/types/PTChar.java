/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.EnumSet;

import org.apache.logging.log4j.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.openpplsoft.runtime.*;

/**
 * Implementation of the PeopleTools char data type.
 */
public final class PTChar extends PTPrimitiveType<Character> {

  private static Logger log = LogManager.getLogger(PTChar.class.getName());
  private static PTTypeConstraint<PTChar> charTc;

  static {
    charTc = new PTTypeConstraint<PTChar>(PTChar.class);
  }

  public PTChar(final char initialVal) {
    super(charTc);
    this.value = initialVal;
  }

  public PTChar(final String initialVal) {
    super(charTc);

    if (initialVal == null) {
      throw new OPSVMachRuntimeException("Initial value is null, "
          + "non-null initial value is required");
    }

    this.write(initialVal);
  }

  public PTChar(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public static PTTypeConstraint<PTChar> getTc() {
    return charTc;
  }

  public void write(final String newValue) {

    this.checkIsWriteable();

    log.debug("Writing to PTChar; raw string is: {}", newValue);

    if (newValue == null) {
      throw new OPSVMachRuntimeException("Initial attempt to write "
          + "null value to PTChar.");
    }

    if(newValue.length() > 1) {
      throw new OPSVMachRuntimeException("Illegal attempt to write string ("
          + newValue + ") to PTChar.");
    }
    if(newValue.length() == 0) {
      throw new OPSVMachRuntimeException("Illegal attempt to write empty "
          + "string to PTChar.");
    }
    this.value = newValue.charAt(0);
  }

  @Override
  public void setBlank() {
    this.value = '\0';
  }

  @Override
  public boolean isBlank() {
    return this.value == '\0';
  }

  @Override
  public void copyValueFrom(final PTPrimitiveType src) {
    if (src instanceof PTChar) {
      this.write(((PTChar) src).read());
    } else if (src instanceof PTString) {
      final String str = ((PTString) src).read();
      if (str.length() > 1) {
        throw new OPSDataTypeException("Cannot write string longer than 1 char to "
            + "PTChar: " + src);
      }
      this.write(str.charAt(0));
    } else {
      throw new OPSDataTypeException("Expected src to be PTChar or PTString.");
    }
  }

  @Override
  public PTBoolean isEqual(final PTPrimitiveType op) {
    if (op instanceof PTChar && this.value.equals(((PTChar) op).read())) {
      return new PTBoolean(true);

    } else if(op instanceof PTString) {
        /*
         * If op is a string of length 1, comparison can continue.
         */
        PTString str = (PTString) op;
        if(str.read().length() == 1
            && this.value.equals(str.read().charAt(0))) {
          return new PTBoolean(true);
        }

    } else if(op instanceof PTInteger) {
        /*
         * If op is an integer, comparison can continue.
         */
        if(Character.getNumericValue(this.value)
            == ((Integer) op.read()).intValue()) {
          return new PTBoolean(true);
        }

    } else {
      throw new OPSDataTypeException("Expected op to be PTChar; is: " + op);
    }
    return new PTBoolean(false);
  }

  @Override
  public PTBoolean isGreaterThan(final PTPrimitiveType op) {
    if (!(op instanceof PTChar)) {
      throw new OPSDataTypeException("Expected op to be PTChar.");
    }
    if (this.value.compareTo(((PTChar) op).read()) > 0) {
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
    if (!(op instanceof PTChar)) {
      throw new OPSDataTypeException("Expected op to be PTChar.");
    }
    if (this.value.compareTo(((PTChar) op).read()) < 0) {
      return new PTBoolean(true);
    }
    return new PTBoolean(false);
  }

  @Override
  public PTBoolean isLessThanOrEqual(final PTPrimitiveType op) {
    throw new OPSDataTypeException("isLessThanOrEqual not supported "
        + "for PTChar.");
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof PTChar)) {
      return false;
    }

    final PTChar other = (PTChar) obj;
    if (this.read().equals(other.read())) {
      return true;
    }
    return false;
  }
}
