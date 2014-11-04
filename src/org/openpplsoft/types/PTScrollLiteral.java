/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.List;

import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;

/**
 * Represents a PeopleTools Scroll literal.
 */
public final class PTScrollLiteral extends PTString {

  public PTScrollLiteral(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public PTScrollLiteral(final String sStr) {
    super(new PTTypeConstraint<PTScrollLiteral>(PTScrollLiteral.class));

    if(!sStr.toLowerCase().startsWith("scroll.")) {
      throw new OPSVMachRuntimeException("Expected sStr to start "
          + "with 'Scroll.' (case-insensitive) while creating "
          + "PTScrollLiteral; sStr = "
          + sStr);
    }
    this.write(sStr.substring(sStr.indexOf(".") + 1));
    this.setReadOnly();
  }
}
