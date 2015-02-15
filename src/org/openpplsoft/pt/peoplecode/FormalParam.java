/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt.peoplecode;

import org.openpplsoft.types.PTAnyTypeConstraint;
import org.openpplsoft.types.PTTypeConstraint;

public class FormalParam {

  private final String id;
  private final PTTypeConstraint typeConstraint;

  public FormalParam(final String i, final PTTypeConstraint tc) {
    this.id = i;

    /*
     * If no type constraint is provided, assume the Any type,
     * as happens in PeopleSoft.
     */
    if (tc == null) {
      this.typeConstraint = new PTAnyTypeConstraint();
    } else {
      this.typeConstraint = tc;
    }
  }

  public String getId() {
    return this.id;
  }

  public PTTypeConstraint getTypeConstraint() {
    return this.typeConstraint;
  }

  public String toString() {
    StringBuilder b = new StringBuilder("FormalParam:id=");
    b.append(this.id);
    b.append(",typeConstraint=").append(this.typeConstraint);
    return b.toString();
  }
}

