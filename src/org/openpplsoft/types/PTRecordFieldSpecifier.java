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
  private InterpretSupervisor interpretSupervisor;

  public PTRecordFieldSpecifier(final Record r, final String fieldName,
      final InterpretSupervisor interpretSupervisor) {
    super(new PTTypeConstraint<PTRecordFieldSpecifier>(PTRecordFieldSpecifier.class));
    this.recDefn = r;
    this.fieldName = fieldName;
    this.interpretSupervisor = interpretSupervisor;
  }

  public String getRecName() {
    return this.recDefn.RECNAME;
  }

  public String getFieldName() {
    return this.fieldName;
  }

  public PTReference<PTBufferField> resolveInCBufferContext() {
    return this.interpretSupervisor.resolveContextualCBufferRecordFieldReference(
        this.recDefn.RECNAME, this.fieldName);
  }

  /**
   * dotProperty calls on record field specifiers should be passed through
   * to the underlying component buffer field in context.
   */
  @Override
  public PTType dotProperty(final String s) {
    return this.resolveInCBufferContext().deref().dotProperty(s);
  }

  /**
   * dotMethod calls on record field specifiers should be passed through
   * to the underlying component buffer field in context.
   */
  @Override
  public Callable dotMethod(final String s) {
    return this.resolveInCBufferContext().deref().dotMethod(s);
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append("recName=").append(this.recDefn.RECNAME);
    b.append(",fieldName=").append(this.fieldName);
    return b.toString();
  }
}
