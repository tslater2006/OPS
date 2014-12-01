/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.buffers.RecordFieldBuffer;
import org.openpplsoft.pt.RecordField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PTFieldTypeConstraint extends PTTypeConstraint<PTField> {

  private static Logger log = LogManager.getLogger(
      PTFieldTypeConstraint.class.getName());

  public PTFieldTypeConstraint() {
    super(PTField.class);
  }

  @Override
  public PTField alloc() {
    throw new OPSDataTypeException("Call to alloc() PTField without args is illegal.");
  }

  public PTStandaloneField allocStandaloneField(final PTStandaloneRecord parentRecord,
      final RecordField rfDefn) {
    return new PTStandaloneField(this, parentRecord, rfDefn);
  }

  public PTBufferField allocBufferField(final PTBufferRecord parentRecord,
      final RecordFieldBuffer recFldBuffer) {
    return new PTBufferField(this, parentRecord, recFldBuffer);
  }

  /**
   * MQUINN 11-30-2014 : Adding temporarily to continue with split process; REMOVE EVENTUALLY.
   */
  public PTBufferField allocBufferField(final PTBufferRecord parentRecord,
      final RecordField rfDefn) {
    return new PTBufferField(this, parentRecord, rfDefn);
  }

  @Override
  public void typeCheck(final PTType a) throws OPSTypeCheckException {
    boolean result = (a instanceof PTNull)
        || (a instanceof PTStandaloneField)
        || (a instanceof PTBufferField);

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
