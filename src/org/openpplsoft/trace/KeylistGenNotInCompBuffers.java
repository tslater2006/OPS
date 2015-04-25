/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class KeylistGenNotInCompBuffers implements IEmission {

  public KeylistGenNotInCompBuffers() {}

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof KeylistGenNotInCompBuffers)) {
      return false;
    }

    // All KeylistGenNotInCompBuffers objects are equal.
    return true;
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 1097, HCB_MULTIPLIER = 311;
    return new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).toHashCode();
  }

  @Override
  public String toString() {
    return "        Not found in component buffers";
  }
}
