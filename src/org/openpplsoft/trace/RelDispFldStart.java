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

public class RelDispFldStart implements IEmission {

  private static Logger log =
      LogManager.getLogger(RelDispFldStart.class.getName());

  private final RecFldName recFldName;

  public RelDispFldStart(final RecFldName recFldName) {
    this.recFldName = recFldName;
  }

  public RelDispFldStart(final String traceFileRecField) {
    this.recFldName = new RecFldName(traceFileRecField);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof RelDispFldStart)) {
      return false;
    }

    final RelDispFldStart other = (RelDispFldStart) obj;
    return this.recFldName.equals(other.recFldName);
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 4211, HCB_MULTIPLIER = 2;

    final HashCodeBuilder hbc = new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.recFldName);
    return hbc.toHashCode();
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("    Starting Related Display processing for - ")
        .append(this.recFldName)
        .toString();
  }
}
