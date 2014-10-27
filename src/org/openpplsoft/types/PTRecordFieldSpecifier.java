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
 * Represents a PeopleTools record field specifier (i.e., a record field name
 * like "DERIVED_REGFRM1.EMPLID")
 * which can be passed to methods like Sort on Rowset objects. Typically generated
 * by dot accesses on RecordSpecifiers.
 */
public final class PTRecordFieldSpecifier extends PTObjectType {

  private Record recDefn;
  private String fieldName;

  public PTRecordFieldSpecifier(final Record r, final String fieldName) {
    super(new PTTypeConstraint<PTRecordFieldSpecifier>(PTRecordFieldSpecifier.class));
    this.recDefn = r;
    this.fieldName = fieldName;
  }

  public String getRecName() {
    return this.recDefn.RECNAME;
  }

  public String getFieldName() {
    return this.fieldName;
  }

  @Override
  public PTType dotProperty(final String s) {
    return null;
  }

  @Override
  public Callable dotMethod(final String s) {
    return null;
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append("recName=").append(this.recDefn.RECNAME);
    b.append("fieldName=").append(this.fieldName);
    return b.toString();
  }
}
