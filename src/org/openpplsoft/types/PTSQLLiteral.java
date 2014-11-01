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
 * Represents a PeopleTools SQL literal.
 */
public final class PTSQLLiteral extends PTString {

  public PTSQLLiteral(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public PTSQLLiteral(final String sStr) {
    super(new PTTypeConstraint<PTSQLLiteral>(PTSQLLiteral.class));

    if(!sStr.toLowerCase().startsWith("sql.")) {
      throw new OPSVMachRuntimeException("Expected sStr to start "
          + "with 'SQL.' (case-insensitive) while creating "
          + "PTSQLLiteral; sStr = "
          + sStr);
    }
    this.write(sStr.substring(sStr.indexOf(".") + 1));
    this.setReadOnly();
  }
}