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

  public PTStandaloneRecord allocStandaloneRecord(final PTStandaloneRow parentRow, final Record recDefn) {
    return new PTStandaloneRecord(this, parentRow, recDefn);
  }

  public PTBufferRecord allocBufferRecord(final PTBufferRow parentRow, final RecordBuffer recBuffer) {
    return new PTBufferRecord(this, parentRow, recBuffer);
  }

  /**
   * MQUINN 11-30-2014 : Adding temporarily for split purposes; REMOVE THIS EVENTUALLY.
   */
  public PTBufferRecord allocBufferRecord(final PTBufferRow parentRow, final Record recDefn) {
    return new PTBufferRecord(this, parentRow, recDefn);
  }

  @Override
  public void typeCheck(final PTType a) throws OPSTypeCheckException {
    boolean result = (a instanceof PTNull)
        || (a instanceof PTStandaloneRecord)
        || (a instanceof PTBufferRecord);

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
