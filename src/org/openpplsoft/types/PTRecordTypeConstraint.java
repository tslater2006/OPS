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

 /**
  * Allocates a new record object with an attached record defn.
  * Allocated records must have an associated record defn in order
  * to determine the type of the value enclosed within them. However, this
  * defn is not part of the type itself; a Record variable can be assigned
  * any Record object, regardless of its underlying record defn (this is why
  * the typeCheck method is not overriden by this class, there's no need to do so).
  * @param recDefn the record defn to attach
  * @return the newly allocated record object
  */
  public PTRecord alloc(final Record recDefn) {
    return new PTRecord(this, recDefn);
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
