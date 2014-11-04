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
    return new PTGrid(this);
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
