/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.text.SimpleDateFormat;

import java.util.Calendar;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.openpplsoft.runtime.*;

/**
 * Implementation of the PeopleTools time data type.
 */
public final class PTTime extends PTPrimitiveType<String> {

  private static final Type staticTypeFlag = Type.TIME;
  private String d;

  /**
   * Constructs a new PTTime object.
   */
  private PTTime() {
    super(staticTypeFlag);

    // default value is current time.
    this.d = new SimpleDateFormat("HH:mm:ss")
        .format(Calendar.getInstance().getTime());
  }

  @Override
  public String read() {
    return this.d;
  }

  @Override
  public String readAsString() {
    return this.d;
  }

  @Override
  public void write(final String newValue) {
    this.checkIsWriteable();
    this.d = newValue;
  }

  @Override
  public void systemWrite(final String newValue) {
    this.checkIsSystemWriteable();
    this.d = newValue;
  }

  @Override
  public void copyValueFrom(final PTPrimitiveType src) {
    throw new EntDataTypeException("copyValueFrom is not yet supported.");
  }

  @Override
  public PTPrimitiveType add(final PTPrimitiveType op) {
    throw new EntDataTypeException("add() not supported.");
  }

  @Override
  public PTPrimitiveType subtract(final PTPrimitiveType op) {
    throw new OPSVMachRuntimeException("subtract() not supported.");
  }

  @Override
  public void setDefault() {
    this.d = null;
  }

  @Override
  public PTBoolean isEqual(final PTPrimitiveType op) {
    throw new EntDataTypeException("isEqual not implemented for "
        + "Date.");
  }

  @Override
  public PTBoolean isGreaterThan(final PTPrimitiveType op) {
    throw new EntDataTypeException("isGreaterThan not implemented for "
        + "Date.");
  }

  @Override
  public PTBoolean isGreaterThanOrEqual(final PTPrimitiveType op) {
    if (!(op instanceof PTTime)) {
      throw new EntDataTypeException("Expected op to be PTTime.");
    }
    if (this.d.compareTo(((PTTime) op).read()) >= 0) {
      return Environment.TRUE;
    }
    return Environment.FALSE;
  }

  @Override
  public PTBoolean isLessThan(final PTPrimitiveType op) {
    throw new EntDataTypeException("isLessThan is not supported for "
        + "Date.");
  }

  @Override
  public PTBoolean isLessThanOrEqual(final PTPrimitiveType op) {
    if (!(op instanceof PTTime)) {
      throw new EntDataTypeException("Expected op to be PTTime.");
    }
    if (this.d.compareTo(((PTTime) op).read()) <= 0) {
      return Environment.TRUE;
    }
    return Environment.FALSE;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof PTTime)) {
      return false;
    }

    final PTTime other = (PTTime) obj;
    if (this.read().equals(other.read())) {
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int HBC_INITIAL = 109, HBC_MULTIPLIER = 67;
    return new HashCodeBuilder(HBC_INITIAL, HBC_MULTIPLIER)
        .append(this.read()).toHashCode();
  }

  public PTPrimitiveType castTo(PTPrimitiveType t) {
    throw new EntDataTypeException("castTo() has not been implemented.");
  }

  @Override
  public boolean typeCheck(final PTType a) {
    return (a instanceof PTTime
        && this.getType() == a.getType());
  }

  /**
   * Retrieves the sentinel object for this type,
   * or creates it if it has not already been created
   * and cached.
   * @return a sentinel object
   */
  public static PTTime getSentinel() {

    // If the sentinel has already been cached, return it immediately.
    final String cacheKey = getCacheKey();
    if (PTType.isSentinelCached(cacheKey)) {
      return (PTTime) PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    final PTTime sentinelObj = new PTTime();
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  @Override
  public PTPrimitiveType alloc() {
    final PTTime newObj = new PTTime();
    PTType.clone(this, newObj);
    return newObj;
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",d=").append(this.d);
    return b.toString();
  }

  private static String getCacheKey() {
    return new StringBuilder(staticTypeFlag.name()).toString();
  }
}
