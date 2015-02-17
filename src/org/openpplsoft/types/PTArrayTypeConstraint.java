/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PTArrayTypeConstraint extends PTTypeConstraint<PTArray> {

  private static Logger log = LogManager.getLogger(
      PTArrayTypeConstraint.class.getName());

  private final int reqdDimension;
  private final PTTypeConstraint reqdNestedTypeConstraint;

  public PTArrayTypeConstraint(final int dim, final PTTypeConstraint nestedTc) {
    super(PTArray.class);
    this.reqdDimension = dim;
    this.reqdNestedTypeConstraint = nestedTc;
  }

  public int getReqdDimension() {
    return this.reqdDimension;
  }

  public PTTypeConstraint getReqdNestedTypeConstraint() {
    return this.reqdNestedTypeConstraint;
  }

  @Override
  public PTArray alloc() {
    return new PTArray(this, this.reqdDimension, this.reqdNestedTypeConstraint);
  }

  @Override
  public void typeCheck(PTType a) throws OPSTypeCheckException {
    boolean result =
        ((a instanceof PTNull)
         || (a instanceof PTArray
              && (this.reqdDimension == ((PTArray) a).getDimensions())
              && this.reqdNestedTypeConstraint.equals(((PTArray) a)
                    .getBaseTypeConstraint())));
    if (!result) {
      throw new OPSTypeCheckException("This type constraint (" + this + ") and "
          + "a (" + a + ") are not type-compatible.");
    }
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof PTArrayTypeConstraint)) {
      return false;
    }

    final PTArrayTypeConstraint other = (PTArrayTypeConstraint) obj;
    return (this.underlyingClass == other.getUnderlyingClass()
        && this.reqdDimension == other.getReqdDimension()
        && this.reqdNestedTypeConstraint.equals(other.getReqdNestedTypeConstraint()));
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 929, HCB_MULTIPLIER = 53;

    return new HashCodeBuilder(HCB_INITIAL, HCB_MULTIPLIER)
      .append(this.underlyingClass).append(this.reqdDimension)
      .append(this.reqdNestedTypeConstraint).toHashCode();
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder(super.toString());
    b.append("[reqdDimension=").append(this.reqdDimension);
    b.append(",reqdNestedTypeConstraint=").append(this.reqdNestedTypeConstraint);
    b.append(']');
    return b.toString();
  }
}
