/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class PTPrimitiveType<T extends java.lang.Object> extends PTType {
  private boolean updatedFlag;
  protected T value;

  protected PTPrimitiveType(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public abstract boolean equals(Object obj);
  public abstract void setDefault();
  public abstract void copyValueFrom(PTPrimitiveType src);

  public abstract PTBoolean isEqual(PTPrimitiveType op);
  public abstract PTBoolean isGreaterThan(PTPrimitiveType op);
  public abstract PTBoolean isGreaterThanOrEqual(PTPrimitiveType op);
  public abstract PTBoolean isLessThan(PTPrimitiveType op);
  public abstract PTBoolean isLessThanOrEqual(PTPrimitiveType op);

  public T read() {
    return this.value;
  }

  public String readAsString() {
    return this.value.toString();
  }

  public void write(T newValue) {
    this.checkIsWriteable();
    this.value = newValue;
  }

  public void systemWrite(T newValue) {
    this.value = newValue;
  }

  protected void markAsUpdated() {
    this.updatedFlag = true;
  }

  public boolean getUpdatedFlag() {
    return this.updatedFlag;
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 41, HCB_MULTIPLIER = 337;

    return new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.value).toHashCode();
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder(super.toString());
    b.append(",value=").append(this.value);
    return b.toString();
  }
}
