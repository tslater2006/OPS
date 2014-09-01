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
 * Represents a PeopleTools menu item literal.
 */
public final class PTMenuItemLiteral extends PTObjectType {

  private String ptITEMNAME;

  public PTMenuItemLiteral(final String iStr) {
    super(new PTTypeConstraint<PTMenuItemLiteral>(PTMenuItemLiteral.class));
    if(!iStr.startsWith("ItemName.")) {
      throw new OPSVMachRuntimeException("Expected iStr to start "
          + "with 'ItemName.' while alloc'ing PTMenuItemLiteral; iStr = "
          + iStr);
    }
    this.ptITEMNAME = iStr.replaceFirst("ItemName.", "");
  }

  /**
   * Returns the name of the menu item represented by this literal.
   * @return the name of the menu item
   */
  public String getMenuItemName() {
    return this.ptITEMNAME;
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
    b.append(",ptITEMNAME=").append(this.ptITEMNAME);
    return b.toString();
  }
}
