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

public final class PTNull extends PTObjectType {

  private static Type staticTypeFlag = Type.NULL;
  private static PTNull singleton;

  private PTNull() {
    super(staticTypeFlag,
        new PTTypeConstraint<PTNull>(PTNull.class));
  }

  public static PTNull getSingleton() {
    if (singleton == null) {
      singleton = new PTNull();
      singleton.setReadOnly();
    }
    return singleton;
  }

  /*
   * Accesses on a defn literal reserved word always
   * resolve to the string itself; i.e., Menu.SA_LEARNER_SERVICES
   * resolves to "SA_LEARNER_SERVICES".
   */
  public PTType dotProperty(String s) {
    throw new EntDataTypeException("Illegal call to dotProperty on PTNull.");
  }

  public Callable dotMethod(String s) {
    throw new EntDataTypeException("Illegal call to dotMethod on PTNull.");
  }

  public boolean typeCheck(PTType a) {
    return (a instanceof PTNull &&
        this.getType() == a.getType());
  }

  @Override
  public String toString() {
    return super.toString();
  }
}

