/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class CRecBuf implements IEmission {

  private static Logger log =
      LogManager.getLogger(CRecBuf.class.getName());

  public final String indentStr, recName, flagStr;
  public final int numFields;

  public CRecBuf(final String indentStr, final String recName,
      final int numFields, final String flagStr) {
    this.indentStr = indentStr;
    this.recName = recName;
    this.numFields = numFields;

    // Flag string can legitimately be empty; will be picked up as null
    // by regex procedure in TraceFileVerifier.
    if (flagStr == null) {
      this.flagStr = "";
    } else {
      this.flagStr = flagStr.trim();
    }
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof CRecBuf)) {
      return false;
    }

    final CRecBuf other = (CRecBuf) obj;
    return this.indentStr.equals(other.indentStr)
        && this.recName.equals(other.recName)
        && this.numFields == other.numFields
        && this.flagStr.equals(other.flagStr);
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 2741, HCB_MULTIPLIER = 229;

    final HashCodeBuilder hbc = new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.indentStr).append(this.recName)
        .append(this.numFields).append(this.flagStr);
    return hbc.toHashCode();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append(this.indentStr).append("CRecBuf ")
        .append(this.recName).append(" fields=")
        .append(this.numFields).append(" ").append(this.flagStr);
    return builder.toString();
  }
}
