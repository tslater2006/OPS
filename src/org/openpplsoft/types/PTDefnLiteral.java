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

  private static PTDefnLiteral singleton;

  private PTDefnLiteral() {
    super(new PTTypeConstraint<PTDefnLiteral>(PTDefnLiteral.class));
  }

  public static PTDefnLiteral getSingleton() {
    if (singleton == null) {
      singleton = new PTDefnLiteral();
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
    return new PTString(s);
  }

  public Callable dotMethod(String s) {
    return null;
  }

  @Override
  public String toString() {
    return super.toString();
  }
}

