/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.openpplsoft.pt.RecFldName;

public class KeylistGenFindingKey implements IEmission {

  private final RecFldName recFldName;

  public KeylistGenFindingKey(final RecFldName recFldName) {
    this.recFldName = recFldName;
  }

  public KeylistGenFindingKey(final String traceFileRecField) {
    this.recFldName = new RecFldName(traceFileRecField);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof KeylistGenFindingKey)) {
      return false;
    }

    final KeylistGenFindingKey other = (KeylistGenFindingKey) obj;
    return this.recFldName.equals(other.recFldName);
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 1619, HCB_MULTIPLIER = 13;
    return new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.recFldName).toHashCode();
  }

  @Override
  public String toString() {
    return "      Keylist generation - Finding value for "
        + this.recFldName;
  }
}
