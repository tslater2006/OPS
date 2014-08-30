/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.pt.peoplecode.AppClassPeopleCodeProg;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PTArrayTypeConstraint extends PTTypeConstraint<PTArray> {

  private static Logger log = LogManager.getLogger(
      PTArrayTypeConstraint.class.getName());

  private int reqdDimension;
  private PTTypeConstraint reqdNestedTypeConstraint;

  public PTArrayTypeConstraint(final int dim, final PTTypeConstraint nestedTc) {
    super(PTArray.class);
    this.reqdDimension = dim;
    this.reqdNestedTypeConstraint = nestedTc;
  }

  public int getReqdDimension() {
    return this.reqdDimension;
  }

  @Override
  public PTArray alloc() {
    return new PTArray(this, this.reqdDimension, this.reqdNestedTypeConstraint);
  }

  @Override
  public boolean typeCheck(PTType a) {
    throw new EntDataTypeException("TODO: Override typeCheck on "
        + "PTArray.");
/*    log.debug("Constraint typecheck: underlying class is {}, a is {}",
      this.underlyingClass, a.getClass());
    return (this.underlyingClass == a.getClass());*/
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder(super.toString());
    b.append("[reqdDimension=").append(this.reqdDimension);
    b.append(",reqdNestedTypeConstraint=").append(this.reqdNestedTypeConstraint);
    b.append("]");
    return b.toString();
  }
}
