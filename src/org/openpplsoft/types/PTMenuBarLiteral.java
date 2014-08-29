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
 * Represents a PeopleTools menu bar literal.
 */
public final class PTMenuBarLiteral extends PTObjectType {

  private static Type staticTypeFlag = Type.MENUBAR_LITERAL;

  private String ptBARNAME;

  public PTMenuBarLiteral(final String bStr) {
    super(staticTypeFlag);
    if(!bStr.startsWith("BarName.")) {
      throw new OPSVMachRuntimeException("Expected bStr to start "
          + "with 'BarName.' while alloc'ing PTMenuBarLiteral; bStr = "
          + bStr);
    }
    this.ptBARNAME = bStr.replaceFirst("BarName.", "");
  }

  /**
   * Returns the name of the menu bar represented by this literal.
   * @return the name of the menu bar
   */
  public String getMenuBarName() {
    return this.ptBARNAME;
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
    return (a instanceof PTMenuBarLiteral
        && this.getType() == a.getType());
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",ptBARNAME=").append(this.ptBARNAME);
    return b.toString();
  }
}
