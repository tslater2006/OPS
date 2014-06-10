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

public class PTRecordLiteral extends PTObjectType {

  private static Type staticTypeFlag = Type.REC_LITERAL;
  public String RECNAME;
  private Record recDefn;

  protected PTRecordLiteral() {
    super(staticTypeFlag);
  }

  protected PTRecordLiteral(Record r) {
    super(staticTypeFlag);
    this.RECNAME = r.RECNAME;
    this.recDefn = r;
  }

  /*
   * Dot accesses on record field literals must
   * always return the appropriate FieldLiteral,
   * assuming the value for s is a valid field on
   * the underlying record.
   */
  public PTType dotProperty(String s) {
    List<RecordField> rfList = this.recDefn.getExpandedFieldList();
    for(RecordField rf : rfList) {
      if(rf.FIELDNAME.equals(s)) {
        return PTFieldLiteral.getSentinel().alloc(this.RECNAME, s);
      }
    }

    throw new EntVMachRuntimeException("Unable to resolve s=" +
      s + " to a field on the PTRecordLiteral for record " +
      this.RECNAME);
  }

  public Callable dotMethod(String s) {
    return null;
  }

  public PTPrimitiveType castTo(PTPrimitiveType t) {
    throw new EntDataTypeException("castTo() has not been implemented.");
  }

  public boolean typeCheck(PTType a) {
    return (a instanceof PTRecordLiteral &&
        this.getType() == a.getType());
  }

  public static PTRecordLiteral getSentinel() {

    // If the sentinel has already been cached, return it immediately.
    String cacheKey = getCacheKey();
    if(PTType.isSentinelCached(cacheKey)) {
      return (PTRecordLiteral)PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    PTRecordLiteral sentinelObj = new PTRecordLiteral();
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  public PTRecordLiteral alloc(Record recDefn) {
    PTRecordLiteral newObj = new PTRecordLiteral(recDefn);
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
    return b.toString();
  }
}
