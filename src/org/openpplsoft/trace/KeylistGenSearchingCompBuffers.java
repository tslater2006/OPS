/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class KeylistGenSearchingCompBuffers implements IEmission {

  private final String fldName;

  public KeylistGenSearchingCompBuffers(final String fldName) {
    this.fldName = fldName;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof KeylistGenSearchingCompBuffers)) {
      return false;
    }

    final KeylistGenSearchingCompBuffers other =
        (KeylistGenSearchingCompBuffers) obj;
    return this.fldName.equals(other.fldName);
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 1013, HCB_MULTIPLIER = 241;
    return new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.fldName).toHashCode();
  }

  @Override
  public String toString() {
    return "                    Seaching for field "
        + this.fldName + " in component buffers";
  }
}
