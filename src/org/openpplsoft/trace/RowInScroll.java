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

public class RowInScroll implements IEmission {

  private static Logger log =
      LogManager.getLogger(RowInScroll.class.getName());

  private final String indentStr;
  private final int rowIdx;

  public RowInScroll(final String indentStr, final int rowIdx) {
    this.indentStr = indentStr;
    this.rowIdx = rowIdx;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof RowInScroll)) {
      return false;
    }

    final RowInScroll other = (RowInScroll) obj;
    return this.indentStr.equals(other.indentStr)
        && this.rowIdx == other.rowIdx;
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 1481, HCB_MULTIPLIER = 37;

    final HashCodeBuilder hbc = new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.indentStr).append(this.rowIdx);
    return hbc.toHashCode();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append(this.indentStr)
        .append("Row ").append(this.rowIdx).append(" at xxxxxxxx.");
    return builder.toString();
  }
}
