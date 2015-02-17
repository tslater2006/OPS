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
 * Represents a PeopleTools GridColumn object.
 */
public final class PTGridColumn extends PTObjectType {

  private static Logger log = LogManager.getLogger(PTGridColumn.class.getName());

  public PTGridColumn(final PTGridColumnTypeConstraint origTc) {
    super(origTc);
  }

  @Override
  public PTType dotProperty(final String s) {
    return null;
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    return b.toString();
  }
}
