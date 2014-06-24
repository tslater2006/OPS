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

public final class PTDefnLiteral extends PTObjectType {

  private static Type staticTypeFlag = Type.DEFN_LITERAL;

  protected PTDefnLiteral() {
    super(staticTypeFlag);
  }

  /*
   * Accesses on a defn literal reserved word always
   * resolve to the string itself; i.e., Menu.SA_LEARNER_SERVICES
   * resolves to "SA_LEARNER_SERVICES".
   */
  public PTType dotProperty(String s) {
    return Environment.getFromLiteralPool(s);
  }

  public Callable dotMethod(String s) {
    return null;
  }

  public PTPrimitiveType castTo(PTPrimitiveType t) {
    throw new EntDataTypeException("castTo() has not been implemented.");
  }

  public boolean typeCheck(PTType a) {
    return (a instanceof PTDefnLiteral &&
        this.getType() == a.getType());
  }

  public static PTDefnLiteral getSentinel() {

    // If the sentinel has already been cached, return it immediately.
    String cacheKey = getCacheKey();
    if(PTType.isSentinelCached(cacheKey)) {
      return (PTDefnLiteral)PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    PTDefnLiteral sentinelObj = new PTDefnLiteral();
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  public PTDefnLiteral alloc() {
    PTDefnLiteral newObj = new PTDefnLiteral();
    PTType.clone(this, newObj);
    return newObj;
  }

  private static String getCacheKey() {
    StringBuilder b = new StringBuilder(staticTypeFlag.name());
    return b.toString();
  }

  @Override
  public String toString() {
    return super.toString();
  }
}

