/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.runtime.OPSVMachRuntimeException;

/**
 * Represents a PeopleTools menu item literal.
 */
public final class PTMenuItemLiteral extends PTString {

  public PTMenuItemLiteral(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public PTMenuItemLiteral(final String iStr) {
    super(new PTTypeConstraint<PTMenuItemLiteral>(PTMenuItemLiteral.class));

    if(!iStr.toLowerCase().startsWith("itemname.")) {
      throw new OPSVMachRuntimeException("Expected iStr to start "
          + "with 'ItemName.' (case-insensitive) while alloc'ing "
          + "PTMenuItemLiteral; iStr = " + iStr);
    }

    this.write(iStr.substring(iStr.indexOf(".") + 1));
  }
}
