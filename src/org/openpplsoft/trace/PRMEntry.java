/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.openpplsoft.pt.RecFldName;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class PRMEntry implements IEmission {

  private static Logger log =
      LogManager.getLogger(PRMEntry.class.getName());

  private final RecFldName recFldName;

  public PRMEntry(final RecFldName recFldName) {
    this.recFldName = recFldName;
  }

  public PRMEntry(final String traceFileRecField) {
    this.recFldName = new RecFldName(traceFileRecField);
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
    return this.recFldName.equals(other.recFldName);
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 3323, HCB_MULTIPLIER = 457;

    final HashCodeBuilder hbc = new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.recFldName);
    return hbc.toHashCode();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("    ").append(this.recFldName);
    return builder.toString();
  }
}
