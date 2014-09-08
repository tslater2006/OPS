/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.pt.Record;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PTAnyTypeConstraint extends PTTypeConstraint<PTAny> {

  private static Logger log = LogManager.getLogger(
      PTAnyTypeConstraint.class.getName());

  public PTAnyTypeConstraint() {
    super(PTAny.class);
  }

  @Override
  public PTAny alloc() {
    return new PTAny(this);
  }

  /**
   * The Any type constraint is compatible with any
   * concrete PeopleTools class and type, regardless
   * of the provided object's underlying class.
   */
  @Override
  public boolean typeCheck(final PTType a) {
    return true;
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
