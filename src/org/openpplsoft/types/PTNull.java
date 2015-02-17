/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.runtime.Callable;

public final class PTNull extends PTObjectType {

  public PTNull(final PTTypeConstraint origTc) {
    super(origTc);
    this.setReadOnly();
  }

  public PTType dotProperty(String s) {
    throw new OPSDataTypeException("Illegal call to dotProperty on PTNull.");
  }

  public Callable dotMethod(String s) {
    throw new OPSDataTypeException("Illegal call to dotMethod on PTNull.");
  }

  @Override
  public String toString() {
    return super.toString();
  }
}

