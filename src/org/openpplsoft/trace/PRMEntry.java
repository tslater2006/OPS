/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class PRMEntry implements IEmission {

  private static Logger log =
      LogManager.getLogger(PRMEntry.class.getName());

  private final String recField;

  public PRMEntry(final String recField) {
    this.recField = recField;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof PRMEntry)) {
      return false;
    }

    final PRMEntry other = (PRMEntry) obj;
    return this.recField.equals(other.recField);
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 3323, HCB_MULTIPLIER = 457;

    final HashCodeBuilder hbc = new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.recField);
    return hbc.toHashCode();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("    ").append(this.recField);
    return builder.toString();
  }
}
