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

public class RecInScroll implements IEmission {

  private static Logger log =
      LogManager.getLogger(RecInScroll.class.getName());

  public String recName;
  public int keyrec, keyfield;

  public RecInScroll(final String recName, final int keyrec,
      final int keyfield) {
    this.recName = recName;
    this.keyrec = keyrec;
    this.keyfield = keyfield;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof RecInScroll)) {
      return false;
    }

    final RecInScroll other = (RecInScroll) obj;
    return this.recName.equals(other.recName)
        && this.keyrec == other.keyrec
        && this.keyfield == other.keyfield;
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 259, HCB_MULTIPLIER = 601;

    final HashCodeBuilder hbc = new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.recName).append(this.keyrec)
        .append(this.keyfield);
    return hbc.toHashCode();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Rec ").append(this.recName)
        .append(" keyrec=").append(this.keyrec)
        .append(" keyfield=").append(this.keyfield);
    return builder.toString();
  }
}
