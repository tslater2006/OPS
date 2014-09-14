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

public final class PTImmutableReference<T extends PTType> extends PTReference<T> {

  public PTImmutableReference(final PTTypeConstraint origTc) {
    super(origTc);
    this.makeImmutable();
  }

  public PTImmutableReference(final PTTypeConstraint origTc, final T initialRef) {
    super(origTc, initialRef);
    this.makeImmutable();
  }
}

