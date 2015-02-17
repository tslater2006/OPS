/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.runtime.OPSVMachRuntimeException;

public final class PTFieldLiteral extends PTString {

  public PTFieldLiteral(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public PTFieldLiteral(final String fStr) {
    super(new PTTypeConstraint<PTFieldLiteral>(PTFieldLiteral.class));

    if(!fStr.toLowerCase().startsWith("field.")) {
      throw new OPSVMachRuntimeException("Expected fStr to start "
          + "with 'Field.' (case-insensitive) while creating PTFieldLiteral; fStr = "
          + fStr);
    }

    this.write(fStr.substring(fStr.indexOf(".") + 1));
    this.setReadOnly();
  }

  public PTFieldLiteral(final String r, final String f) {
    super(new PTTypeConstraint<PTFieldLiteral>(PTFieldLiteral.class));

    this.write(r + "." + f);
    this.setReadOnly();
  }

  public String getRecordName() {
    if (this.read().indexOf(".") != -1) {
      return this.read().substring(0, this.read().indexOf("."));
    }

    return null;
  }

  public String getFieldName() {
    if (this.read().indexOf(".") != -1) {
      return this.read().substring(this.read().indexOf(".") + 1);
    }
    return this.read();
  }
}
