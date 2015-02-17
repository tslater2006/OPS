/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents a PeopleTools Grid object.
 */
public final class PTGrid extends PTObjectType {

  private static Logger log = LogManager.getLogger(PTGrid.class.getName());

  private final String pageName, gridName;

  public PTGrid(final PTGridTypeConstraint origTc,
      final PTPageLiteral pageName, final PTString gridName) {
    super(origTc);
    this.pageName = pageName.read();
    this.gridName = gridName.read();
  }

  @Override
  public PTType dotProperty(final String s) {
    return null;
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",pageName=").append(this.pageName);
    b.append(",gridName=").append(this.gridName);
    return b.toString();
  }
}
