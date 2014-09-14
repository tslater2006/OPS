/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.pt.*;
import java.util.*;
import org.openpplsoft.runtime.*;

public class PTReference<T extends PTType> extends PTType {

  protected T referencedValue;
  private boolean isImmutable;

  public PTReference(final PTTypeConstraint origTc) {
    super(origTc);

    if (origTc.isUnderlyingClassPrimitive()) {
      throw new OPSVMachRuntimeException("Illegal attempt to create a "
          + "reference with a primitive type constraint without providing "
          + "an initial value for the reference to point to.");
    }
  }

  public PTReference(final PTTypeConstraint origTc, final T initialRef) {
    super(origTc);
    this.pointTo(initialRef);

    // References to primitives are always immutable.
    if (origTc.isUnderlyingClassPrimitive()) {
      this.makeImmutable();
    }
  }

  public void pointTo(final T newRef) {
    if (this.isImmutable) {
      throw new OPSVMachRuntimeException("Illegal attempt to point immutable "
          + "reference " + this + " to new object: " + newRef);
    }
    if (this.getOriginatingTypeConstraint().typeCheck(newRef)) {
      this.referencedValue = newRef;
    } else {
      throw new OPSVMachRuntimeException("Unable to point reference to newRef; "
          + "newRef (" + newRef + ") is not type compatible with this "
          + "reference's (" + this + ") underlying type constraint.");
    }
  }

  public T deref() {
    return this.referencedValue;
  }

  public void makeImmutable() {
    this.isImmutable = true;
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    if (this.isImmutable) {
      b.append("[*IMMUTABLE_REF*]");
    } else {
      b.append("[*MUTABLE_REF*]");
    }
    b.append(super.toString()).append(";referencedValue=").append(this.referencedValue);
    return b.toString();
  }
}
