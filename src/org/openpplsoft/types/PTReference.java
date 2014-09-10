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

public final class PTReference extends PTObjectType {

  private PTType referencedValue;

  public PTReference() {
    super(new PTTypeConstraint<PTReference>(PTReference.class));
    this.referencedValue = PTNull.getSingleton();
  }

  public PTReference(final PTType initialRef) {
    super(new PTTypeConstraint<PTReference>(PTReference.class));
    this.referencedValue = initialRef;
  }

  public void pointTo(final PTType newRef) {
    this.referencedValue = newRef;
  }

  public PTType deref() {
    return this.referencedValue;
  }

  public PTType dotProperty(String s) {
    throw new OPSDataTypeException("Illegal call to dotProperty on PTReference.");
  }

  public Callable dotMethod(String s) {
    throw new OPSDataTypeException("Illegal call to dotMethod on PTReference.");
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder("[*REF*]");
    b.append(super.toString()).append(";referencedValue=").append(this.referencedValue);
    return b.toString();
  }
}

