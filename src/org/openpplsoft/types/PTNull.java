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

public final class PTNull extends PTObjectType {

  private static PTNull singleton;

  private PTNull() {
    super(new PTTypeConstraint<PTNull>(PTNull.class));
  }

  public static PTNull getSingleton() {
    if (singleton == null) {
      singleton = new PTNull();
      singleton.setReadOnly();
    }
    return singleton;
  }

  public PTType dotProperty(String s) {
    throw new OPSDataTypeException("Illegal call to dotProperty on PTNull.");
  }

  public Callable dotMethod(String s) {
    throw new OPSDataTypeException("Illegal call to dotMethod on PTNull.");
  }

  @Override
  public String toString() {
    return super.toString();
  }
}

