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

public class CRecBuf implements IEmission {

  private static Logger log =
      LogManager.getLogger(CRecBuf.class.getName());

  public final String indentStr, recName;

  public CRecBuf(final String indentStr, final String recName) {
    this.indentStr = indentStr;
    this.recName = recName;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof CRecBuf)) {
      return false;
    }

    final CRecBuf other = (CRecBuf) obj;
    return this.indentStr.equals(other.indentStr)
        && this.recName.equals(other.recName);
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 2741, HCB_MULTIPLIER = 229;

    final HashCodeBuilder hbc = new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.indentStr).append(this.recName);
    return hbc.toHashCode();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append(this.indentStr).append("CRecBuf ")
        .append(this.recName);
    return builder.toString();
  }
}
