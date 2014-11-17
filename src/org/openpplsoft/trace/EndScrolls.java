/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EndScrolls implements IEmission {

  public EndScrolls() {}

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof EndScrolls)) {
      return false;
    }

    final EndScrolls other = (EndScrolls) obj;
    return true;
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 11, HCB_MULTIPLIER = 1259;

    final HashCodeBuilder hbc = new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER);
    return hbc.toHashCode();
  }

  @Override
  public String toString() {
    return "End Scrolls";
  }
}
