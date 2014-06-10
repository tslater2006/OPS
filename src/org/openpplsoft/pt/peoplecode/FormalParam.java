/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt.peoplecode;

import org.openpplsoft.types.*;

public class FormalParam {

  public PTType type;
  public String id;

  public FormalParam(PTType t, String i) {
    this.type = t;
    this.id = i;
  }

  public String toString() {
    StringBuilder b = new StringBuilder(this.id);
    b.append(":").append(this.type);
    return b.toString();
  }
}

