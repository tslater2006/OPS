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

  public String RECNAME;
  public String FIELDNAME;

  public PTFieldLiteral(String fStr) {
    super(new PTTypeConstraint<PTFieldLiteral>(PTFieldLiteral.class));

    if(!fStr.toLowerCase().startsWith("field.")) {
      throw new OPSVMachRuntimeException("Expected fStr to start "
          + "with 'Field.' (case-insensitive) while creating PTFieldLiteral; fStr = "
          + fStr);
    }

    this.FIELDNAME = fStr.substring(fStr.indexOf(".") + 1);
  }

  public PTFieldLiteral(String r, String f) {
    super(new PTTypeConstraint<PTFieldLiteral>(PTFieldLiteral.class));
    this.RECNAME = r;
    this.FIELDNAME = f;
  }

  public PTType dotProperty(String s) {
    throw new OPSVMachRuntimeException("dotProperty not supported.");
  }

  public Callable dotMethod(String s) {
    throw new OPSVMachRuntimeException("dotMethod not supported.");
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder(super.toString());
    b.append(",RECNAME=").append(this.RECNAME);
    b.append(",FIELDNAME=").append(this.FIELDNAME);
    return b.toString();
  }
}
