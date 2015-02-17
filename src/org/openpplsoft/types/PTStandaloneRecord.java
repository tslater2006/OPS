/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.Record;
import org.openpplsoft.pt.RecordField;
import org.openpplsoft.runtime.OPSVMachRuntimeException;

/**
 * Represents a PeopleTools standalone record (not in the component buffer).
 */
public final class PTStandaloneRecord extends PTRecord<PTStandaloneRow,
    PTStandaloneField> {

  private static Logger log =
      LogManager.getLogger(PTStandaloneRecord.class.getName());

  public PTStandaloneRecord(final PTRecordTypeConstraint origTc,
      final PTStandaloneRow pRow, final Record r) {
    super(origTc);
    this.parentRow = pRow;
    this.recDefn = r;

    // this map is linked in order to preserve
    // the order in which fields are added.
    this.fieldRefs = new LinkedHashMap<>();
    this.fieldRefIdxTable = new TreeMap<>();
    int i = 1;
    for (final RecordField rf : this.recDefn.getExpandedFieldList()) {
      PTFieldTypeConstraint fldTc = new PTFieldTypeConstraint();

      try {
        final PTImmutableReference<PTStandaloneField> newFldRef =
            new PTImmutableReference<>(fldTc,
                fldTc.allocStandaloneField(this, rf));
        this.fieldRefs.put(rf.getFldName(), newFldRef);
        this.fieldRefIdxTable.put(i++, newFldRef);
      } catch (final OPSTypeCheckException opstce) {
        throw new OPSVMachRuntimeException(opstce.getMessage(), opstce);
      }
    }
  }

  @Override
  public String toString() {
    return "[STANDALONE]" + super.toString();
  }
}
