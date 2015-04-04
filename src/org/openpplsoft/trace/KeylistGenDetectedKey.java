/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class KeylistGenDetectedKey implements IEmission {

  private final String keyName;

  public KeylistGenDetectedKey(final String keyName) {
    this.keyName = keyName;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof KeylistGenDetectedKey)) {
      return false;
    }

    final KeylistGenDetectedKey other = (KeylistGenDetectedKey) obj;
    return this.keyName.equals(other.keyName);
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 3163, HCB_MULTIPLIER = 257;
    return new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.keyName).toHashCode();
  }

  @Override
  public String toString() {
    return "      Keylist generation - " + this.keyName + " is a key";
  }
}
