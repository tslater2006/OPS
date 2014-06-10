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

/*
 * I believe rows can contain rowsets and multiple records.
 * The exact details are still unclear to me. TODO: Keep this in mind.
 */
public class PTRow extends PTObjectType {

  private static Type staticTypeFlag = Type.ROW;
  public PTRecord record;

  protected PTRow() {
    super(staticTypeFlag);
  }

  protected PTRow(PTRecord rec) {
    super(staticTypeFlag);
    this.record = rec;
  }

  public PTType dotProperty(String s) {
    if(this.record.recDefn.RECNAME.equals(s)) {
      return this.record;
    }
    return null;
  }

  public Callable dotMethod(String s) {
    return null;
  }

  public PTPrimitiveType castTo(PTPrimitiveType t) {
    throw new EntDataTypeException("castTo() has not been implemented.");
  }

  public boolean typeCheck(PTType a) {
    return (a instanceof PTRow &&
        this.getType() == a.getType());
  }

  /*
   * Calls to make a row read-only should make its
   * record read-only as well.
   */
  @Override
  public PTType setReadOnly() {
    super.setReadOnly();
    if(this.record != null) {
      this.record.setReadOnly();
    }
    return this;
  }

  public static PTRow getSentinel() {

    // If the sentinel has already been cached, return it immediately.
    String cacheKey = getCacheKey();
    if(PTType.isSentinelCached(cacheKey)) {
      return (PTRow)PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    PTRow sentinelObj = new PTRow();
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  public PTRow alloc(PTRecord rec) {
    PTRow newObj = new PTRow(rec);
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
    b.append(",record=").append(this.record);
    return b.toString();
  }
}
