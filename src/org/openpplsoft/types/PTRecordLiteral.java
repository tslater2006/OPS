/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.List;

import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;

/**
 * Represents a PeopleTools record literal.
 */
public final class PTRecordLiteral extends PTObjectType {

  private String ptRECNAME;
  private Record recDefn;

  public PTRecordLiteral(final String rStr) {
    super(new PTTypeConstraint<PTRecordLiteral>(PTRecordLiteral.class));

    if(!rStr.toLowerCase().startsWith("record.")) {
      throw new OPSVMachRuntimeException("Expected rStr to start "
          + "with 'Record.' (case-insensitive) while creating PTRecordLiteral; rStr = "
          + rStr);
    }
    Record r = DefnCache.getRecord(rStr.substring(rStr.indexOf(".") + 1));
    this.ptRECNAME = r.RECNAME;
    this.recDefn = r;
  }

  public PTRecordLiteral(final Record r) {
    super(new PTTypeConstraint<PTRecordLiteral>(PTRecordLiteral.class));
    this.ptRECNAME = r.RECNAME;
    this.recDefn = r;
  }

  /**
   * Returns the name of the record represented by this literal.
   * @return the name of the record
   */
  public String getRecName() {
    return this.ptRECNAME;
  }

  /**
   * Dot accesses on record field literals must
   * always return the appropriate FieldLiteral,
   * assuming the value for s is a valid field on
   * the underlying record.
   * @param s the name of the property
   * @return the FieldLiteral corresponding to {@code s}
   */
  @Override
  public PTType dotProperty(final String s) {
    final List<RecordField> rfList = this.recDefn.getExpandedFieldList();
    for (RecordField rf : rfList) {
      if (rf.FIELDNAME.equals(s)) {
        return new PTFieldLiteral(this.ptRECNAME, s);
      }
    }

    throw new OPSVMachRuntimeException("Unable to resolve s="
        + s + " to a field on the PTRecordLiteral for record "
        + this.ptRECNAME);
  }

  @Override
  public Callable dotMethod(final String s) {
    return null;
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",ptRECNAME=").append(this.ptRECNAME);
    return b.toString();
  }
}
