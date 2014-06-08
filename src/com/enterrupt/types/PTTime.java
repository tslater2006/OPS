/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package com.enterrupt.types;

import java.util.*;
import java.text.SimpleDateFormat;
import com.enterrupt.runtime.*;

public class PTTime extends PTPrimitiveType<String> {

  private static Type staticTypeFlag = Type.TIME;
  private String d;

  protected PTTime() {
    super(staticTypeFlag);

    // default value is current time.
    d = new SimpleDateFormat("HH:mm:ss")
        .format(Calendar.getInstance().getTime());
  }

  public String read() {
    return this.d;
  }

  public void write(String newValue) {
    this.checkIsWriteable();
    this.d = newValue;
  }

  public void systemWrite(String newValue) {
    this.checkIsSystemWriteable();
    this.d = newValue;
  }

  public void copyValueFrom(PTPrimitiveType src) {
    throw new EntDataTypeException("copyValueFrom is not yet supported.");
  }

  public PTPrimitiveType add(PTPrimitiveType op) {
    throw new EntDataTypeException("add() not supported.");
  }

  public PTPrimitiveType subtract(PTPrimitiveType op) {
    throw new EntVMachRuntimeException("subtract() not supported.");
  }

  public void setDefault() {
    this.d = null;
  }

  public PTBoolean isEqual(PTPrimitiveType op) {
    throw new EntDataTypeException("isEqual not implemented for " +
        "Date.");
  }

  public PTBoolean isGreaterThan(PTPrimitiveType op) {
    throw new EntDataTypeException("isGreaterThan not implemented for " +
        "Date.");
  }

  public PTBoolean isGreaterThanOrEqual(PTPrimitiveType op) {
    if(!(op instanceof PTTime)) {
      throw new EntDataTypeException("Expected op to be PTTime.");
    }
    if(this.d.compareTo(((PTTime)op).read()) >= 0) {
      return Environment.TRUE;
    }
    return Environment.FALSE;
  }

  public PTBoolean isLessThan(PTPrimitiveType op) {
    throw new EntDataTypeException("isLessThan is not supported for " +
        "Date.");
  }

  public PTBoolean isLessThanOrEqual(PTPrimitiveType op) {
    if(!(op instanceof PTTime)) {
      throw new EntDataTypeException("Expected op to be PTTime.");
    }
    if(this.d.compareTo(((PTTime)op).read()) <= 0) {
      return Environment.TRUE;
    }
    return Environment.FALSE;
  }

  public boolean equals(Object obj) {
    if(obj == this)
      return true;
    if(obj == null)
      return false;
    if(!(obj instanceof PTTime))
      return false;

    PTTime other = (PTTime)obj;
    if(this.read().equals(other.read())) {
      return true;
    }
    return false;
  }

  public boolean typeCheck(PTType a) {
    return (a instanceof PTTime &&
        this.getType() == a.getType());
  }

  public static PTTime getSentinel() {

    // If the sentinel has already been cached, return it immediately.
    String cacheKey = getCacheKey();
    if(PTType.isSentinelCached(cacheKey)) {
      return (PTTime)PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    PTTime sentinelObj = new PTTime();
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  public PTPrimitiveType alloc() {
    PTTime newObj = new PTTime();
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
    b.append(",d=").append(this.d);
    return b.toString();
  }
}
