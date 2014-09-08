/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.runtime.*;

public class PTAny extends PTObjectType {

  private PTType enclosedValue;

  protected PTAny(final PTTypeConstraint origTc) {
    super(origTc);
    this.enclosedValue = PTNull.getSingleton();
  }

  public void setEnclosedValue(final PTType v) {
    this.enclosedValue = v;
  }

  @Override
  public PTType dotProperty(final String s) {
    throw new OPSVMachRuntimeException("dotProperty not supported "
        + "for PTAny.");
  }

  @Override
  public Callable dotMethod(final String s) {
    throw new OPSVMachRuntimeException("dotMethod not supported "
        + "for PTAny.");
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(":enclosedValue=").append(this.enclosedValue);
    return b.toString();
  }
}
