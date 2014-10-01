/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.buffers.*;
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
    throw new OPSDataTypeException("Call to alloc() PTRowset without args is illegal.");
  }

  // Use to allocate standalone (non-component buffer) rowsets.
  public PTRowset alloc(final PTRow parentRow, final Record primaryRecDefn) {
    return new PTRowset(this, parentRow, primaryRecDefn);
  }

  // Use to allocate component buffer rowsets.
  public PTRowset alloc(final PTRow parentRow, final ScrollBuffer scrollDefn) {
    return new PTRowset(this, parentRow, scrollDefn);
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
