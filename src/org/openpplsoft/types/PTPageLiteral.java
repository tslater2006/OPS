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
 * Represents a PeopleTools page literal.
 */
public final class PTPageLiteral extends PTString {

  public PTPageLiteral(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public PTPageLiteral(final String pStr) {
    super(new PTTypeConstraint<PTPageLiteral>(PTPageLiteral.class));

    if(!pStr.toLowerCase().startsWith("page.")) {
      throw new OPSVMachRuntimeException("Expected pStr to start "
          + "with 'Page.' (case-insensitive) while alloc'ing PTPageLiteral; pStr = "
          + pStr);
    }

    this.write(pStr.substring(pStr.indexOf(".") + 1));
  }

  /**
   * Returns the name of the page represented by this literal.
   * @return the name of the page
   */
  public String getPageName() {
    return this.read();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(",literal=").append(this.read());
    return b.toString();
  }
}
