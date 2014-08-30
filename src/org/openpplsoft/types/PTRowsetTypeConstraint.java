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

public class PTRowsetTypeConstraint extends PTTypeConstraint<PTRowset> {

  private static Logger log = LogManager.getLogger(
      PTRowsetTypeConstraint.class.getName());

  public PTRowsetTypeConstraint() {
    super(PTRowset.class);
  }

  @Override
  public PTRowset alloc() {
    throw new EntDataTypeException("Call to alloc() PTRowset from type constraint "
        + "without providing associated record defn is illegal.");
  }

  public PTRowset alloc(final Record recDefn) {
    return new PTRowset(this, recDefn);
  }

  @Override
  public boolean typeCheck(PTType a) {
    throw new EntDataTypeException("TODO: Override typeCheck on "
        + "PTRowsetTypeConstraint.");
/*    log.debug("Constraint typecheck: underlying class is {}, a is {}",
      this.underlyingClass, a.getClass());
    return (this.underlyingClass == a.getClass());*/
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
