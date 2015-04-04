/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class KeylistGenFoundInRecord implements IEmission {

  private final String recName, fldName;

  public KeylistGenFoundInRecord(final String recName,
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
    } else if (!(obj instanceof KeylistGenFoundInRecord)) {
      return false;
    }

    final KeylistGenFoundInRecord other = (KeylistGenFoundInRecord) obj;
    return this.recName.equals(other.recName)
        && this.fldName.equals(other.fldName);
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 1609, HCB_MULTIPLIER = 5;
    return new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.recName)
        .append(this.fldName).toHashCode();
  }

  @Override
  public String toString() {
    return "            Field " + this.fldName + " found in record "
        + this.recName;
  }
}
