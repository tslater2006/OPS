/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

public final class PTCallFrameBoundary extends PTType {

  private static PTCallFrameBoundary singleton;

  static {
    singleton = new PTCallFrameBoundary();
    singleton.setReadOnly();
  }

  private PTCallFrameBoundary() {
    super(Type.CALL_FRAME_BOUNDARY);
  }

  public static PTType getSentinel() {
    return singleton;
  }

  public PTPrimitiveType castTo(PTPrimitiveType t) {
    throw new EntDataTypeException("castTo() has not been implemented.");
  }

  @Override
  public boolean typeCheck(final PTType a) {
    return (a instanceof PTCallFrameBoundary
        && this.getType() == a.getType());
  }

  @Override
  public String toString() {
    return "|=====" + super.toString();
  }
}
