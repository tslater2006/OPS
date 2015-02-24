/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class KeylistGenNotInKeyBuffer implements IEmission {

  public KeylistGenNotInKeyBuffer() {}

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof KeylistGenNotInKeyBuffer)) {
      return false;
    }

    // All KeylistGenNotInKeyBuffer objects are equal.
    return true;
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 499, HCB_MULTIPLIER = 227;
    return new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).toHashCode();
  }

  @Override
  public String toString() {
    return "                Not Found in key buffer";
  }
}
