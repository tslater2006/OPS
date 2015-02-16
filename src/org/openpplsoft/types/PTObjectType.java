/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.lang.reflect.Method;

import org.openpplsoft.runtime.*;

public abstract class PTObjectType extends PTType {

  protected PTObjectType(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public abstract PTType dotProperty(String s);

  public Callable dotMethod(final String methodName) {
    try {
      final Method method = this.getClass().getMethod(methodName, new Class[0]);
      return new Callable(method, this);
    } catch (final NoSuchMethodException nsme) {
      return null;
    }
  }
}
