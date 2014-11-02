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

  public static PeopleCodeException create(final int msgSetNbr, final int msgNbr,
      final String[] msgBindVals) {
    final MsgSet msgSet = DefnCache.getMsgSet(msgSetNbr);
    return new PeopleCodeException(msgSet.getMessage(msgNbr));
  }

  private PeopleCodeException(final String msg) {
    super(msg);
  }
}
