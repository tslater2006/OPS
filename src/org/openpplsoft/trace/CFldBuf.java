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

public class CFldBuf implements IEmission {

  private static Logger log =
      LogManager.getLogger(CFldBuf.class.getName());

  private final String indentStr, fldName, fldValue, flagStr;

  public CFldBuf(final String indentStr,
      final String fldName, final String fldValue, final String flagStr) {
    this.indentStr = indentStr;
    this.fldName = fldName;

    // The empty string will read as null from the tracefile, whereas
    // OPS will use the empty string; handle this here:
    if (fldValue == null) {
      this.fldValue = "";
    } else {
      this.fldValue = fldValue;
    }

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
    } else if (!(obj instanceof CFldBuf)) {
      return false;
    }

    final CFldBuf other = (CFldBuf) obj;

/*    log.debug("This fldValue: {}", this.fldValue.length());
    for (int i = 0; i < this.fldValue.length(); i++) {
      log.debug("{}: {}", i, Character.getNumericValue(this.fldValue.charAt(i)));
    }

    log.debug("Other fldValue: {}", other.fldValue.length());
    for (int i = 0; i < other.fldValue.length(); i++) {
      log.debug("{}: {}", i, Character.getNumericValue(other.fldValue.charAt(i)));
    }*/

    return this.indentStr.equals(other.indentStr)
        && this.fldName.equals(other.fldName)
        && this.fldValue.equals(other.fldValue)
        && this.flagStr.equals(other.flagStr);
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 4441, HCB_MULTIPLIER = 89;

    final HashCodeBuilder hbc = new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.indentStr).append(this.fldName)
        .append(this.fldValue).append(this.flagStr);
    return hbc.toHashCode();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append(this.indentStr).append(this.fldName)
        .append("='").append(this.fldValue).append("'; ")
        .append(this.flagStr);
    return builder.toString();
  }
}
