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
    throw new OPSDataTypeException("Call to alloc() PTRow without args is illegal.");
  }

  public PTRow alloc(final PTRowset parentRowset, final Set<Record> s) {
    return new PTRow(this, parentRowset, s);
  }

  public PTRow alloc(final PTRowset parentRowset, final Record r) {
    return new PTRow(this, parentRowset, r);
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
