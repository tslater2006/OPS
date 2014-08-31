/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

public abstract class PTPrimitiveType<T> extends PTType {

  protected PTPrimitiveType(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public abstract T read();
  public abstract String readAsString();
  public abstract void write(T newValue);
  public abstract void systemWrite(T newValue);

  public abstract boolean equals(Object obj);
  public abstract int hashCode();
  public abstract void setDefault();
  public abstract void copyValueFrom(PTPrimitiveType src);

  public abstract PTBoolean isEqual(PTPrimitiveType op);
  public abstract PTBoolean isGreaterThan(PTPrimitiveType op);
  public abstract PTBoolean isGreaterThanOrEqual(PTPrimitiveType op);
  public abstract PTBoolean isLessThan(PTPrimitiveType op);
  public abstract PTBoolean isLessThanOrEqual(PTPrimitiveType op);

  public abstract PTPrimitiveType add(PTPrimitiveType op);
  public abstract PTPrimitiveType subtract(PTPrimitiveType op);
  public abstract PTPrimitiveType mul(PTPrimitiveType op);
  public abstract PTPrimitiveType div(PTPrimitiveType op);
}
