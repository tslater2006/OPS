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
 * Represents a PeopleTools page literal.
 */
public final class PTPageLiteral extends PTObjectType {

  private static Type staticTypeFlag = Type.PAGE_LITERAL;

  private String ptPNLNAME;

  public PTPageLiteral(final String pStr) {
    super(staticTypeFlag,
        new PTTypeConstraint<PTPageLiteral>(PTPageLiteral.class));
    if(!pStr.startsWith("Page.")) {
      throw new OPSVMachRuntimeException("Expected pStr to start "
          + "with 'Page.' while alloc'ing PTPageLiteral; pStr = "
          + pStr);
    }
    this.ptPNLNAME = pStr.replaceFirst("Page.", "");
  }

  /**
   * Returns the name of the page represented by this literal.
   * @return the name of the page
   */
  public String getPageName() {
    return this.ptPNLNAME;
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
    return (a instanceof PTPageLiteral
        && this.getType() == a.getType());
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",ptPNLNAME=").append(this.ptPNLNAME);
    return b.toString();
  }
}
