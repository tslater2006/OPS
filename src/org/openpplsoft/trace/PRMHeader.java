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

public class PRMHeader implements IEmission {

  private static Logger log =
      LogManager.getLogger(PRMHeader.class.getName());

  private final String componentName, langCode, market;
  private final int prmEntryCount;

  public PRMHeader(final String componentName, final String langCode,
      final String market, final int prmEntryCount) {
    this.componentName = componentName;
    this.langCode = langCode;
    this.market = market;
    this.prmEntryCount = prmEntryCount;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof PRMHeader)) {
      return false;
    }

    final PRMHeader other = (PRMHeader) obj;
    return this.componentName.equals(other.componentName)
        && this.langCode.equals(other.langCode)
        && this.market.equals(other.market)
        && this.prmEntryCount == other.prmEntryCount;
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 571, HCB_MULTIPLIER = 313;

    final HashCodeBuilder hbc = new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.componentName).append(this.langCode)
        .append(this.market).append(this.prmEntryCount);
    return hbc.toHashCode();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("PRM ").append(this.componentName).append(".")
        .append(this.langCode).append(".").append(this.market)
        .append(" version xx count=").append(this.prmEntryCount);
    return builder.toString();
  }
}
