/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.pt.*;
import java.util.*;
import org.openpplsoft.runtime.*;

public class PTFieldLiteral extends PTObjectType {

  private static Type staticTypeFlag = Type.FLD_LITERAL;
  public String RECNAME;
  public String FIELDNAME;

  protected PTFieldLiteral() {
    super(staticTypeFlag);
  }

  protected PTFieldLiteral(String f) {
    super(staticTypeFlag);
    this.FIELDNAME = f;
  }

  protected PTFieldLiteral(String r, String f) {
    super(staticTypeFlag);
    this.RECNAME = r;
    this.FIELDNAME = f;
  }

  public PTType dotProperty(String s) {
    throw new EntVMachRuntimeException("dotProperty not supported.");
  }

  public Callable dotMethod(String s) {
    throw new EntVMachRuntimeException("dotMethod not supported.");
  }

  public PTPrimitiveType castTo(PTPrimitiveType t) {
    throw new EntDataTypeException("castTo() has not been implemented.");
  }

  public boolean typeCheck(PTType a) {
    return (a instanceof PTFieldLiteral &&
      this.getType() == a.getType());
  }

  public static PTFieldLiteral getSentinel() {

    // If the sentinel has already been cached, return it immediately.
    String cacheKey = getCacheKey();
    if(PTType.isSentinelCached(cacheKey)) {
      return (PTFieldLiteral)PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    PTFieldLiteral sentinelObj = new PTFieldLiteral();
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  public PTFieldLiteral alloc(String FIELDNAME) {
    PTFieldLiteral newObj = new PTFieldLiteral(FIELDNAME);
    PTType.clone(this, newObj);
    return newObj;
  }

  public PTFieldLiteral alloc(String RECNAME, String FIELDNAME) {
    PTFieldLiteral newObj = new PTFieldLiteral(RECNAME, FIELDNAME);
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
    b.append(",RECNAME=").append(this.RECNAME);
    b.append(",FIELDNAME=").append(this.FIELDNAME);
    return b.toString();
  }
}
