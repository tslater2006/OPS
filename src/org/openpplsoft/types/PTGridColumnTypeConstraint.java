/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PTGridColumnTypeConstraint extends PTTypeConstraint<PTGridColumn> {

  private static Logger log = LogManager.getLogger(
      PTGridColumnTypeConstraint.class.getName());

  public PTGridColumnTypeConstraint() {
    super(PTGridColumn.class);
  }

  @Override
  public PTGridColumn alloc() {
    return new PTGridColumn(this);
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
