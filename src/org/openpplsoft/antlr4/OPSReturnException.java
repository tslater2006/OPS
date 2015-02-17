/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.antlr4;

import org.openpplsoft.runtime.OPSVMachRuntimeException;

/**
 * This exception is thrown by the interpreter when a PeopleCode
 * return statement is encountered; the exception will subsequently
 * be caught by the InterpreterSupervisor, which will clean up after
 * execution differently than if the return stmt had not been present.
 */
public class OPSReturnException extends OPSVMachRuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates an instance of the exception.
   * @param instructionText the text of the return stmt in the
   *    PeopleCode program.
   */
  public OPSReturnException(final String instructionText) {
    super(instructionText);
  }
}
