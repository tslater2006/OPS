/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.buffers.RecordBuffer;
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
    throw new OPSDataTypeException("Call to alloc() PTRecord without args is illegal.");
  }

  // Used to allocate records without an associated buffer in the component.
  public PTRecord alloc(final PTRow parentRow, final Record recDefn) {
    return new PTRecord(this, parentRow, recDefn);
  }

  // Used to allocate records with an associated buffer in the component.
  public PTRecord alloc(final PTRow parentRow, final RecordBuffer recBuffer) {
    return new PTRecord(this, parentRow, recBuffer);
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
