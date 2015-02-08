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

public class ScrollIndex implements IEmission {

  private static Logger log =
      LogManager.getLogger(ScrollIndex.class.getName());

  private final String indentStr;
  private final int index;

  public ScrollIndex(final String indentStr, final int index) {
    this.indentStr = indentStr;
    this.index = index;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof ScrollIndex)) {
      return false;
    }

    final ScrollIndex other = (ScrollIndex) obj;
    return this.indentStr.equals(other.indentStr)
        && this.index == other.index;
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 3719, HCB_MULTIPLIER = 227;

    final HashCodeBuilder hbc = new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.indentStr).append(this.index);
    return hbc.toHashCode();
  }

  @Override
  public String toString() {
    return this.indentStr + "Scroll " + this.index;
  }
}
