/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class BeginLevel implements IEmission {

  private final String indentStr;
  public final int levelIdx, rowIdx, occursCount, activeCount,
      hiddenCount, scrollCount, numRecs;
  public final boolean noAutoSelectFlag, noAutoUpdateFlag;

  public BeginLevel(
      final String indentStr,
      final int levelIdx,
      final int rowIdx,
      final int occursCount,
      final int activeCount,
      final int hiddenCount,
      final int scrollCount,
      final boolean noAutoSelectFlag,
      final boolean noAutoUpdateFlag,
      final int numRecs) {
    this.indentStr = indentStr;
    this.levelIdx = levelIdx;
    this.rowIdx = rowIdx;
    this.occursCount = occursCount;
    this.activeCount = activeCount;
    this.hiddenCount = hiddenCount;
    this.scrollCount = scrollCount;
    this.noAutoSelectFlag = noAutoSelectFlag;
    this.noAutoUpdateFlag = noAutoUpdateFlag;
    this.numRecs = numRecs;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof BeginLevel)) {
      return false;
    }

    final BeginLevel other = (BeginLevel) obj;
    return this.indentStr.equals(other.indentStr)
        && this.levelIdx == other.levelIdx
        && this.rowIdx == other.rowIdx
        && this.occursCount == other.occursCount
        && this.activeCount == other.activeCount
        && this.hiddenCount == other.hiddenCount
        && this.noAutoSelectFlag == other.noAutoSelectFlag
        && this.noAutoUpdateFlag == other.noAutoUpdateFlag
        && this.numRecs == other.numRecs;
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 3, HCB_MULTIPLIER = 1097;

    final HashCodeBuilder hbc = new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER)
        .append(this.indentStr)
        .append(this.levelIdx)
        .append(this.rowIdx)
        .append(this.occursCount)
        .append(this.activeCount)
        .append(this.hiddenCount)
        .append(this.scrollCount)
        .append(this.noAutoSelectFlag)
        .append(this.noAutoUpdateFlag)
        .append(this.numRecs);
    return hbc.toHashCode();
  }

  @Override
  public String toString() {

    final StringBuilder builder = new StringBuilder();
    builder.append(this.indentStr)
        .append("Begin level ").append(this.levelIdx)
        .append("[row ").append(this.rowIdx).append("]")
        .append(" occcnt=").append(this.occursCount)
        .append(" activecnt=").append(this.activeCount)
        .append(" hiddencnt=").append(this.hiddenCount)
        .append(" scrlcnt=").append(this.scrollCount)
        .append(" flags=xxxx");
    if (this.noAutoSelectFlag) {
      builder.append(" noautoselect");
    }
    if (this.noAutoUpdateFlag) {
      builder.append(" noautoupdate");
    }
    builder.append(" nrec=").append(this.numRecs);
    return builder.toString();
  }
}
