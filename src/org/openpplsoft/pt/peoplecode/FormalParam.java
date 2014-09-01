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

  public String id;
  public PTTypeConstraint typeConstraint;

  public FormalParam(final String i, final PTTypeConstraint tc) {
    this.id = i;
    this.typeConstraint = tc;

    /*
     * If no type constraint is provided, assume the Any type,
     * as happens in PeopleSoft.
     */
    if (this.typeConstraint == null) {
      this.typeConstraint = new PTAnyTypeConstraint();
    }
  }

  public String toString() {
    StringBuilder b = new StringBuilder("FormalParam:id=");
    b.append(this.id);
    b.append(",typeConstraint=").append(this.typeConstraint);
    return b.toString();
  }
}

