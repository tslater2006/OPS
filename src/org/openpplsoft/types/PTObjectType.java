/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.runtime.*;

public abstract class PTObjectType extends PTType {

  protected PTObjectType(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public abstract PTType dotProperty(String s);
  public abstract Callable dotMethod(String s);
}
