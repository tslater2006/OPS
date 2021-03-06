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

  public PTStandaloneRowset allocStandaloneRowset(final PTStandaloneRow parentRow,
      final Record primaryRecDefn) {
    return new PTStandaloneRowset(this, parentRow, primaryRecDefn);
  }

  public PTBufferRowset allocBufferRowset(final SearchRecordBuffer searchRecBuf) {
    return new PTBufferRowset(this, searchRecBuf);
  }

  /**
   * This method allows for the root component buffer rowset to be intialized;
   * remember that the level 0 rowset is a *child* rowset of the rowset
   * allocated by this method.
   */
  public PTBufferRowset allocBufferRowset(final PTBufferRow parentRow,
      final ScrollBuffer scrollDefn) {
    return new PTBufferRowset(this, parentRow, scrollDefn);
  }

  @Override
  public void typeCheck(final PTType a) throws OPSTypeCheckException {
    boolean result = (a instanceof PTNull)
        || (a instanceof PTStandaloneRowset)
        || (a instanceof PTBufferRowset);

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
