/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.pt.Record;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PTRowTypeConstraint extends PTTypeConstraint<PTRow> {

  private static Logger log = LogManager.getLogger(
      PTRowTypeConstraint.class.getName());

  public PTRowTypeConstraint() {
    super(PTRow.class);
  }

  @Override
  public PTRow alloc() {
    throw new EntDataTypeException("Call to alloc() PTRow from type constraint "
        + "without providing associated set of record defns is illegal.");
  }

  public PTRow alloc(final Set<Record> s) {
    return new PTRow(this, s);
  }

  @Override
  public boolean typeCheck(PTType a) {
    throw new EntDataTypeException("TODO: Override typeCheck on "
        + "PTRowTypeConstraint.");
/*    log.debug("Constraint typecheck: underlying class is {}, a is {}",
      this.underlyingClass, a.getClass());
    return (this.underlyingClass == a.getClass());*/
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
