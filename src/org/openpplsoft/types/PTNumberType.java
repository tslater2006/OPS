/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

public abstract class PTNumberType<T extends java.lang.Number> extends PTPrimitiveType<T> {

  protected PTNumberType(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public abstract PTNumberType add(PTNumberType op);
  public abstract PTNumberType sub(PTNumberType op);
  public abstract PTNumberType mul(PTNumberType op);
  public abstract PTNumberType div(PTNumberType op);
}
