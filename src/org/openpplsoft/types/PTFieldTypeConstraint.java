/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

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
    throw new OPSDataTypeException("Call to alloc() PTField from type constraint "
        + "without providing associated record field defn is illegal.");
  }

  public PTField alloc(final RecordField rfDefn) {
    return new PTField(this, rfDefn);
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
