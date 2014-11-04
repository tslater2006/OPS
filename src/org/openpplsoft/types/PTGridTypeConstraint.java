/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PTGridTypeConstraint extends PTTypeConstraint<PTGrid> {

  private static Logger log = LogManager.getLogger(
      PTGridTypeConstraint.class.getName());

  public PTGridTypeConstraint() {
    super(PTGrid.class);
  }

  @Override
  public PTGrid alloc() {
    throw new OPSDataTypeException("Illegal attempt to alloc Grid "
        + "object without providing page and grid names; must call other "
        + "(overloaded) alloc method and provide these values.");
  }

  public PTGrid alloc(final PTPageLiteral pageName, final PTString gridName) {
    return new PTGrid(this, pageName, gridName);
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
