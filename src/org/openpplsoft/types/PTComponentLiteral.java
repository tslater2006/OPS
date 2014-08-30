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
 * Represents a PeopleTools component literal.
 */
public final class PTComponentLiteral extends PTObjectType {

  private static Type staticTypeFlag = Type.COMPONENT_LITERAL;

  private String ptPNLGRPNAME;

  public PTComponentLiteral(final String cStr) {
    super(staticTypeFlag,
        new PTTypeConstraint<PTComponentLiteral>(PTComponentLiteral.class));
    if(!cStr.startsWith("Component.")) {
      throw new OPSVMachRuntimeException("Expected cStr to start "
          + "with 'Component.' while creating PTComponentLiteral; cStr = "
          + cStr);
    }
    this.ptPNLGRPNAME = cStr.replaceFirst("Component.", "");
  }

  /**
   * Returns the name of the component represented by this literal.
   * @return the name of the component
   */
  public String getComponentName() {
    return this.ptPNLGRPNAME;
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
    return (a instanceof PTComponentLiteral
        && this.getType() == a.getType());
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",ptPNLGRPNAME=").append(this.ptPNLGRPNAME);
    return b.toString();
  }
}
