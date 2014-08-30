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

public class PTRecordTypeConstraint extends PTTypeConstraint<PTRecord> {

  private static Logger log = LogManager.getLogger(
      PTRecordTypeConstraint.class.getName());

  public PTRecordTypeConstraint() {
    super(PTRecord.class);
  }

  @Override
  public PTRecord alloc() {
    throw new EntDataTypeException("Call to alloc() PTRecord from type constraint "
        + "without providing associated record defn is illegal.");
  }

  public PTRecord alloc(final Record recDefn) {
    return new PTRecord(this, recDefn);
  }

  @Override
  public boolean typeCheck(PTType a) {
    throw new EntDataTypeException("TODO: Override typeCheck on "
        + "PTRecordTypeConstraint.");
/*    log.debug("Constraint typecheck: underlying class is {}, a is {}",
      this.underlyingClass, a.getClass());
    return (this.underlyingClass == a.getClass());*/
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
