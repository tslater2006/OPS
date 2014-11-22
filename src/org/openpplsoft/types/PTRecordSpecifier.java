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
 * Represents a PeopleTools record specifier (i.e., a record name like
 * "DERIVED_REGFRM1" that can either be passed to methods itself, or
 * resolved (via dot access) to a RecordFieldSpecifier like "DERIVED_REGFRM1.EMPLID",
 * which in turn can be passed to methods like Sort on Rowset objects.
 */
public final class PTRecordSpecifier extends PTObjectType {

  private Record recDefn;
  private InterpretSupervisor interpretSupervisor;

  public PTRecordSpecifier(final Record r,
      final InterpretSupervisor interpretSupervisor) {
    super(new PTTypeConstraint<PTRecordSpecifier>(PTRecordSpecifier.class));
    this.recDefn = r;
    this.interpretSupervisor = interpretSupervisor;
  }

  /**
   * Returns the name of the record represented by this specifier.
   * @return the name of the record
   */
  public String getRecName() {
    return this.recDefn.RECNAME;
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
        return new PTRecordFieldSpecifier(this.recDefn, s, this.interpretSupervisor);
      }
    }

    throw new OPSVMachRuntimeException("Unable to resolve s="
        + s + " to a field on the PTRecordSpecifier for record "
        + this.recDefn.RECNAME);
  }

  @Override
  public Callable dotMethod(final String s) {
    return null;
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append("recName=").append(this.recDefn.RECNAME);
    return b.toString();
  }
}
