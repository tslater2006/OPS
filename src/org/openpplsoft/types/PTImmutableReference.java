/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

public final class PTImmutableReference<T extends PTType> extends PTReference<T> {

  /**
   * Creating a reference must involve an inital value to refer to,
   * otherwise this.referencedValue will be null (Java null) and
   * will cause NPEs when callers use the result of deref(). Do not
   * create a constructor that does not accept an arg of type T for
   * use as the initial value to point to.
   */
  public PTImmutableReference(final PTTypeConstraint origTc, final T initialRef)
      throws OPSTypeCheckException {
    super(origTc, initialRef);
    this.makeImmutable();
  }
}

