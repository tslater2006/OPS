/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class KeylistGenScanningLevel implements IEmission {

  private final int level;

  public KeylistGenScanningLevel(final int level) {
    this.level = level;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof KeylistGenScanningLevel)) {
      return false;
    }

    final KeylistGenScanningLevel other = (KeylistGenScanningLevel) obj;
    return this.level == other.level;
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 3359, HCB_MULTIPLIER = 617;
    return new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.level).toHashCode();
  }

  @Override
  public String toString() {
    return "          Scanning level " + this.level;
  }
}
