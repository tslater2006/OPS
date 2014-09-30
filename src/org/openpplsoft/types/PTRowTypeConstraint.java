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

  /**
   * DO NOT FORGET that allocating a row is always done within the context of a
   * rowset, and that the row must have the same registered record and child scroll
   * definitions as its parent rowset. Do not make additional versions of alloc()
   * here without considering what could go wrong if certain args are ommitted or
   * defaulted incorrectly.
   */
  public PTRow alloc(final PTRowset parentRowset, final Set<Record> recordDefns,
      final Map<String, ScrollBuffer> childScrollDefns) {
    return new PTRow(this, parentRowset, recordDefns, childScrollDefns);
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
