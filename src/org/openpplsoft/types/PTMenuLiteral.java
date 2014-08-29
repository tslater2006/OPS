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
public final class PTMenuLiteral extends PTObjectType {

  private static Type staticTypeFlag = Type.MENU_LITERAL;

  private String ptMENUNAME;
  private Menu menuDefn;

  public PTMenuLiteral(final Menu m) {
    super(staticTypeFlag);
    this.ptMENUNAME = m.getMenuName();
    this.menuDefn = m;
  }

  public PTMenuLiteral(final String mStr) {
    super(staticTypeFlag);
    if(!mStr.startsWith("MenuName.")) {
      throw new OPSVMachRuntimeException("Expected mStr to start "
          + "with 'Menu.' while creating PTMenuLiteral; mStr = "
          + mStr);
    }
    Menu m = DefnCache.getMenu(mStr.replaceFirst("MenuName.", ""));
    this.ptMENUNAME = m.getMenuName();
    this.menuDefn = m;
  }

  /**
   * Returns the name of the menu represented by this literal.
   * @return the name of the menu
   */
  public String getMenuName() {
    return this.ptMENUNAME;
  }

  @Override
  public PTType dotProperty(final String s) {
    throw new EntDataTypeException("dotProperty() has not been implemented.");
  }

  @Override
  public Callable dotMethod(final String s) {
    return null;
  }

  @Override
  public boolean typeCheck(final PTType a) {
    return (a instanceof PTMenuLiteral
        && this.getType() == a.getType());
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",ptMENUNAME=").append(this.ptMENUNAME);
    return b.toString();
  }
}
