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

 /**
  * Allocates a new rowset object with an attached record defn.
  * Allocated rowsets must have an associated record defn in order
  * to determine the type of records enclosed within them. However, this
  * defn is not part of the type itself; a Rowset variable can be assigned
  * any Rowset object, regardless of its underlying record defn (this is why
  * the typeCheck and equals/hashCode methods are not overriden by this class,
  * there's no need to do so).
  * @param recDefn the record defn to attach
  * @return the newly allocated rowset object
  */
  public PTRowset alloc(final Record recDefn) {
    return new PTRowset(this, recDefn);
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
