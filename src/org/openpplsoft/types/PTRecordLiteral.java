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
 * Represents a PeopleTools record literal.
 */
public final class PTRecordLiteral extends PTString {

  public PTRecordLiteral(final PTTypeConstraint origTc) {
    super(origTc);
  }

  public PTRecordLiteral(final String rStr) {
    super(new PTTypeConstraint<PTRecordLiteral>(PTRecordLiteral.class));

    if(!rStr.toLowerCase().startsWith("record.")) {
      throw new OPSVMachRuntimeException("Expected rStr to start "
          + "with 'Record.' (case-insensitive) while creating PTRecordLiteral; rStr = "
          + rStr);
    }
    this.write(rStr.substring(rStr.indexOf(".") + 1));
    this.setReadOnly();
  }
}
