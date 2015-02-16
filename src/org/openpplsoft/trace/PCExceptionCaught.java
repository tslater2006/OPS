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


/**
 * Represents lines in tracefile indicating
 * that a PeopleCode exception was caught.
 */
public class PCExceptionCaught implements IEmission {

  private static Logger log =
      LogManager.getLogger(PCExceptionCaught.class.getName());

  private final int msgSetNbr, msgNbr;
  private final String exMsg, responsibleProgAndEvent,
      responsibleMethodOrFuncName;

  // NOTE: responsibleMethodOrFuncName may legitimately
  // be null if exception was thrown
  // from the root stmt list of a program.
  public PCExceptionCaught(final String exMsg, int msgSetNbr,
      int msgNbr, final String responsibleProgAndEvent,
      final String responsibleMethodOrFuncName) {
    this.exMsg = exMsg;
    this.msgSetNbr = msgSetNbr;
    this.msgNbr = msgNbr;
    this.responsibleProgAndEvent = responsibleProgAndEvent;
    this.responsibleMethodOrFuncName = responsibleMethodOrFuncName;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof PCExceptionCaught)) {
      return false;
    }

    final PCExceptionCaught other = (PCExceptionCaught) obj;

    return (this.exMsg.trim().equals(other.exMsg.trim())
        && this.msgSetNbr == other.msgSetNbr
        && this.msgNbr == other.msgNbr
        && this.responsibleProgAndEvent.equals(other.responsibleProgAndEvent)
        && this.responsibleMethodOrFuncName.equals(other.responsibleMethodOrFuncName));
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 1523, HCB_MULTIPLIER = 31;

    final HashCodeBuilder hbc = new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.exMsg)
        .append(this.msgSetNbr).append(this.msgNbr)
        .append(this.responsibleProgAndEvent).append(this.responsibleMethodOrFuncName);

    return hbc.toHashCode();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Caught Exception: ").append(this.exMsg);
    builder.append(" (").append(this.msgSetNbr);
    builder.append(",").append(this.msgNbr);
    builder.append(") ").append(this.responsibleProgAndEvent);
    // May be null if exception was thrown in the root stmt list of a program.
    if (this.responsibleMethodOrFuncName != null) {
      builder.append(" Name:").append(this.responsibleMethodOrFuncName);
    }
    return builder.toString();
  }
}
