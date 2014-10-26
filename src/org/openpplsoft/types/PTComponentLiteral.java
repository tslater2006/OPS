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

  private String ptPNLGRPNAME;

  public PTComponentLiteral(final String cStr) {
    super(new PTTypeConstraint<PTComponentLiteral>(PTComponentLiteral.class));

    if(!cStr.toLowerCase().startsWith("component.")) {
      throw new OPSVMachRuntimeException("Expected cStr to start "
          + "with 'Component.' (case-insensitive) while creating "
          + "PTComponentLiteral; cStr = "
          + cStr);
    }
    this.ptPNLGRPNAME = cStr.substring(cStr.indexOf(".") + 1);
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
    throw new OPSDataTypeException("dotProperty() has not been implemented.");
  }

  @Override
  public Callable dotMethod(final String s) {
    return null;
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",ptPNLGRPNAME=").append(this.ptPNLGRPNAME);
    return b.toString();
  }
}
