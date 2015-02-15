/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import org.openpplsoft.pt.MsgSet;

/**
 * This exception is thrown by the interpreter when a PeopleCode
 * exception is thrown.
 */
public class PeopleCodeException extends OPSVMachRuntimeException {

  private ExecContext responsibleExecContext;
  private int msgSetNbr, msgNbr;

  public static PeopleCodeException create(final ExecContext responsibleExecContext,
      final int msgSetNbr, final int msgNbr,
      final String[] msgBindVals) {
    final MsgSet msgSet = DefnCache.getMsgSet(msgSetNbr);
    return new PeopleCodeException(responsibleExecContext,
        msgSet.getMessage(msgNbr, msgBindVals), msgSetNbr, msgNbr);
  }

  private PeopleCodeException(final ExecContext responsibleExecContext,
      final String msg, final int msgSetNbr, final int msgNbr) {
    super(msg);
    this.responsibleExecContext = responsibleExecContext;
    this.msgSetNbr = msgSetNbr;
    this.msgNbr = msgNbr;
  }

  public int getMsgSetNbr() {
    return this.msgSetNbr;
  }

  public int getMsgNbr() {
    return this.msgNbr;
  }

  public String getResponsibleProgAndEvent() {
    // The regex removes the first part of the descriptor (i.e., we don't
    // want "AppClassPC" in "AppClassPC.EO.CA...")
    return this.responsibleExecContext.getProg()
        .getDescriptor().replaceFirst(".*?\\.", "");
  }

  public String getResponsibleMethodOrFuncName() {
    return this.responsibleExecContext.getMethodOrFuncName();
  }
}
