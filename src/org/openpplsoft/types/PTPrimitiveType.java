/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

public abstract class PTPrimitiveType<T> extends PTType {

  protected PTPrimitiveType(Type t) {
    super(t);
  }

  public abstract T read();
  public abstract void write(T newValue);
  public abstract void systemWrite(T newValue);

  public abstract PTPrimitiveType alloc();
  public abstract boolean equals(Object obj);
  public abstract int hashCode();
  public abstract boolean typeCheck(PTType a);
  public abstract void setDefault();
  public abstract void copyValueFrom(PTPrimitiveType src);

  public abstract PTBoolean isEqual(PTPrimitiveType op);
  public abstract PTBoolean isGreaterThan(PTPrimitiveType op);
  public abstract PTBoolean isGreaterThanOrEqual(PTPrimitiveType op);
  public abstract PTBoolean isLessThan(PTPrimitiveType op);
  public abstract PTBoolean isLessThanOrEqual(PTPrimitiveType op);

  public abstract PTPrimitiveType add(PTPrimitiveType op);
  public abstract PTPrimitiveType subtract(PTPrimitiveType op);

  protected void checkIsWriteable() {
    if(this.isSentinel()) {
      throw new EntDataTypeException("Attempted illegal write to a " +
          "sentinel PTType object.");
    }
    if(this.getFlags().contains(TFlag.READONLY)) {
      throw new EntDataTypeException("Attempted illegal write to a " +
          "readonly PTType object.");
    }
  }

  protected void checkIsSystemWriteable() {
    if(this.isSentinel()) {
      throw new EntDataTypeException("Attempted illegal system write " +
          "to a sentinel PTType object.");
    }
  }
}
