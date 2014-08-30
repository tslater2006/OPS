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

public final class PTFieldLiteral extends PTObjectType {

  private static Type staticTypeFlag = Type.FLD_LITERAL;
  public String RECNAME;
  public String FIELDNAME;

  public PTFieldLiteral(String f) {
    super(staticTypeFlag,
        new PTTypeConstraint<PTFieldLiteral>(PTFieldLiteral.class));
    this.FIELDNAME = f;
  }

  public PTFieldLiteral(String r, String f) {
    super(staticTypeFlag,
        new PTTypeConstraint<PTFieldLiteral>(PTFieldLiteral.class));
    this.RECNAME = r;
    this.FIELDNAME = f;
  }

  public PTType dotProperty(String s) {
    throw new OPSVMachRuntimeException("dotProperty not supported.");
  }

  public Callable dotMethod(String s) {
    throw new OPSVMachRuntimeException("dotMethod not supported.");
  }

  public boolean typeCheck(PTType a) {
    return (a instanceof PTFieldLiteral &&
      this.getType() == a.getType());
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder(super.toString());
    b.append(",RECNAME=").append(this.RECNAME);
    b.append(",FIELDNAME=").append(this.FIELDNAME);
    return b.toString();
  }
}
