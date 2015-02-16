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

public final class PTDefnLiteralPrefix extends PTObjectType {

  private final PTDefnLiteralKeyword prefixKeyword;

  public PTDefnLiteralPrefix(final PTDefnLiteralKeyword k) {
    super(new PTTypeConstraint<PTDefnLiteralPrefix>(PTDefnLiteralPrefix.class));
    this.prefixKeyword = k;
  }

  /*
   * Accesses on a defn literal prefix always
   * resolve to the approprtiate type literal object; i.e., a "Menu." prefix
   * object resolves to a MenuLiteral object of "SA_LEARNER_SERVICES" when
   * passed "SA_LEARNER_SERVICES" as the argument below.
   */
  public PTType dotProperty(final String s) {
    return this.prefixKeyword.allocLiteralObj(s);
  }

  public Callable dotMethod(final String s) {
    return null;
  }

  @Override
  public String toString() {
    return super.toString();
  }
}

