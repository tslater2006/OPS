/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.List;

import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;

/**
 * Represents a PeopleTools menu literal.
 */
public final class PTMenuLiteral extends PTString {

  public PTMenuLiteral(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public PTMenuLiteral(final String mStr) {
    super(new PTTypeConstraint<PTMenuLiteral>(PTMenuLiteral.class));

    if(!mStr.toLowerCase().startsWith("menuname.")) {
      throw new OPSVMachRuntimeException("Expected mStr to start "
          + "with 'Menu.' (case-insensitive) while creating PTMenuLiteral; mStr = "
          + mStr);
    }
    this.write(mStr.substring(mStr.indexOf(".") + 1));
    this.setReadOnly();
  }
}
