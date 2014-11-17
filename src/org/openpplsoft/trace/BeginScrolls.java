/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class BeginScrolls implements IEmission {

  // i.e., "Search Results", "After ScrollSelect", etc.
  private String phase;

  public BeginScrolls(final String phase) {
    this.phase = phase;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof BeginScrolls)) {
      return false;
    }

    final BeginScrolls other = (BeginScrolls) obj;
    return this.phase.equals(other.phase);
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 79, HCB_MULTIPLIER = 619;

    final HashCodeBuilder hbc = new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.phase);
    return hbc.toHashCode();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Begin Scrolls ").append(this.phase);
    return builder.toString();
  }
}
