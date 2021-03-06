/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PTAnyTypeConstraint extends PTTypeConstraint<PTType> {

  private static Logger log = LogManager.getLogger(
      PTAnyTypeConstraint.class.getName());

  public PTAnyTypeConstraint() {
    super(PTType.class);
  }

  @Override
  public PTType alloc() {
    return new PTNull(this);
  }

  /**
   * The Any type constraint is compatible with any
   * concrete PeopleTools class and type, regardless
   * of the provided object's underlying class.
   */
  @Override
  public void typeCheck(final PTType a) {
    // Do nothing; never throws type check exception b/c everything
    // is type-compatible with Any.
  }

  @Override
  public String toString() {
    return "(PTAnyTypeConstraint)" + super.toString();
  }
}
