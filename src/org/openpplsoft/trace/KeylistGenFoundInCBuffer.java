/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class KeylistGenFoundInCBuffer implements IEmission {

  private final String value;

  public KeylistGenFoundInCBuffer(final String value) {
    if (value == null) {
      this.value = "";
    } else {
      this.value = value;
    }
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof KeylistGenFoundInCBuffer)) {
      return false;
    }

    final KeylistGenFoundInCBuffer other = (KeylistGenFoundInCBuffer) obj;
    return this.value.equals(other.value);
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 1987, HCB_MULTIPLIER = 29;
    return new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.value).toHashCode();
  }

  @Override
  public String toString() {
    return "        Found in component buffers, value = "
        + this.value;
  }
}
