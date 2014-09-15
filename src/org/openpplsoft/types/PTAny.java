/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.runtime.*;

/**
 * An identifier of type Any accepts objects and primitives
 * of any type; Any is not a concrete type, thus it extends
 * neither PTPrimitiveType nor PTObjectType.
 */
public class PTAny extends PTType {

  private PTType enclosedValue;

  protected PTAny(final PTTypeConstraint origTc) {
    super(origTc);
    this.enclosedValue = PTNull.getSingleton();
  }

  public void setEnclosedValue(final PTType v) {
    this.enclosedValue = v;
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(":enclosedValue=").append(this.enclosedValue);
    return b.toString();
  }
}
