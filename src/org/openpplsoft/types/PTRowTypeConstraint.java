/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.pt.Record;
import org.openpplsoft.buffers.ScrollBuffer;

import java.util.Set;
import java.util.Map;

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

  public PTStandaloneRow allocStandaloneRow(final PTStandaloneRowset parentRowset) {
    return new PTStandaloneRow(this, parentRowset);
  }

  public PTBufferRow allocBufferRow(final PTBufferRowset parentRowset,
      final Set<Record> recordDefns,
      final Map<String, ScrollBuffer> childScrollDefns) {
    return new PTBufferRow(this, parentRowset, recordDefns, childScrollDefns);
  }

  @Override
  public void typeCheck(final PTType a) throws OPSTypeCheckException {
    boolean result = (a instanceof PTNull)
        || (a instanceof PTStandaloneRow)
        || (a instanceof PTBufferRow);

    if (!result) {
      throw new OPSTypeCheckException("This type constraint (" + this + ") and "
          + "a (" + a + ") are not type-compatible.");
    }
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
