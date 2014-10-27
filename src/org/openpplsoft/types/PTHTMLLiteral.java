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
 * Represents a PeopleTools HTML literal.
 */
public final class PTHTMLLiteral extends PTString {

  public PTHTMLLiteral(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public PTHTMLLiteral(final String hStr) {
    super(PTString.getTc());

    if(!hStr.toLowerCase().startsWith("html.")) {
      throw new OPSVMachRuntimeException("Expected hStr to start "
          + "with 'HTML.' (case-insensitive) while creating "
          + "PTHTMLLiteral; hStr = "
          + hStr);
    }
    this.write(hStr.substring(hStr.indexOf(".") + 1));
    this.setReadOnly();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",literal=").append(this.read());
    return b.toString();
  }
}
