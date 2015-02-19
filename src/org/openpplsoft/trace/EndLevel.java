/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EndLevel implements IEmission {

  private final String indentStr;
  private final int levelIdx, rowIdx;

  public EndLevel(
      final String indentStr,
      final int levelIdx,
      final int rowIdx) {
    this.indentStr = indentStr;
    this.levelIdx = levelIdx;
    this.rowIdx = rowIdx;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof EndLevel)) {
      return false;
    }

    final EndLevel other = (EndLevel) obj;
    return this.indentStr.equals(other.indentStr)
        && this.levelIdx == other.levelIdx
        && this.rowIdx == other.rowIdx;
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 1549, HCB_MULTIPLIER = 829;

    final HashCodeBuilder hbc = new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER)
        .append(this.indentStr)
        .append(this.levelIdx)
        .append(this.rowIdx);
    return hbc.toHashCode();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append(this.indentStr)
        .append("End level ").append(this.levelIdx)
        .append("[row ").append(this.rowIdx).append(']');
    return builder.toString();
  }
}
