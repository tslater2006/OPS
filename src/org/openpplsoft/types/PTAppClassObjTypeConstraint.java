/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.openpplsoft.pt.peoplecode.AppClassPeopleCodeProg;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PTAppClassObjTypeConstraint extends PTTypeConstraint<PTAppClassObj> {

  private static Logger log = LogManager.getLogger(
      PTAppClassObjTypeConstraint.class.getName());

  private AppClassPeopleCodeProg requiredProgDefn;

  public PTAppClassObjTypeConstraint(final AppClassPeopleCodeProg p) {
    super(PTAppClassObj.class);
    this.requiredProgDefn = p;
  }

  public AppClassPeopleCodeProg getReqdProgDefn() {
    return this.requiredProgDefn;
  }

  @Override
  public PTAppClassObj alloc() {
    return new PTAppClassObj(this, this.requiredProgDefn);
  }

  /**
   * Type checks on app class objects must involve the associated
   * program definitions; if the objects are not of the same program
   * defn, the type check must fail.
   * @param a the typed object to type check
   * @returns true if types match, false otherwise.
   */
  @Override
  public boolean typeCheck(PTType a) {
    return (a instanceof PTAppClassObj
        && (this.requiredProgDefn == ((PTAppClassObj) a).progDefn));
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof PTAppClassObjTypeConstraint)) {
      return false;
    }

    final PTAppClassObjTypeConstraint other = (PTAppClassObjTypeConstraint) obj;
    return (this.underlyingClass == other.getUnderlyingClass()
        && this.requiredProgDefn == other.getReqdProgDefn());
  }

  @Override
  public int hashCode() {
    final int HBC_INITIAL = 1009, HBC_MULTIPLIER = 179;

    return new HashCodeBuilder(HBC_INITIAL, HBC_MULTIPLIER)
      .append(this.underlyingClass).append(this.requiredProgDefn).toHashCode();
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder(super.toString());
    b.append("[requiredProgDefn=").append(this.requiredProgDefn.getDescriptor()).append("]");
    return b.toString();
  }
}