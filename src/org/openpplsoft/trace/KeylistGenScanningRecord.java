/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class KeylistGenScanningRecord implements IEmission {

  private final String recName, fldName;

  public KeylistGenScanningRecord(final String recName,
      final String fldName) {
    this.recName = recName;
    this.fldName = fldName;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof KeylistGenScanningRecord)) {
      return false;
    }

    final KeylistGenScanningRecord other = (KeylistGenScanningRecord) obj;
    return this.recName.equals(other.recName)
        && this.fldName.equals(other.fldName);
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 809, HCB_MULTIPLIER = 67;
    return new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.recName)
        .append(this.fldName).toHashCode();
  }

  @Override
  public String toString() {
    return "                        Scanning record " + recName + " for field "
        + this.fldName;
  }
}
