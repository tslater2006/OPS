/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.runtime.OPSVMachRuntimeException;

/**
 * Represents a PeopleTools HTML literal.
 */
public final class PTHTMLLiteral extends PTString {

  public PTHTMLLiteral(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public PTHTMLLiteral(final String hStr) {
    super(new PTTypeConstraint<PTHTMLLiteral>(PTHTMLLiteral.class));

    if(!hStr.toLowerCase().startsWith("html.")) {
      throw new OPSVMachRuntimeException("Expected hStr to start "
          + "with 'HTML.' (case-insensitive) while creating "
          + "PTHTMLLiteral; hStr = "
          + hStr);
    }
    this.write(hStr.substring(hStr.indexOf(".") + 1));
    this.setReadOnly();
  }
}
