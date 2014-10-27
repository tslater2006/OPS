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

  /**
   * Returns the name of the menu represented by this literal.
   * @return the name of the menu
   */
  public String getMenuName() {
    return this.read();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",literal=").append(this.read());
    return b.toString();
  }
}
