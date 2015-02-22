/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents the demarcation emission in PS
 * tracefiles signaling the start of execution
 * of a PeopleCode program.
 */
public class RelDispStart implements IEmission {

  public RelDispStart() {}

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof RelDispStart)) {
      return false;
    }

    // All RelDispStart objects are equal.
    return true;
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 1103, HCB_MULTIPLIER = 983;

    return new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).toHashCode();
  }

  @Override
  public String toString() {
    return "Starting Related Display processing";
  }
}
