/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.antlr4;

import org.openpplsoft.runtime.OPSVMachRuntimeException;

/**
 * This exception is thrown by the interpreter when a function impl
 * body is encountered; the exception will subsequently
 * be caught by the InterpreterSupervisor, which will look at the execution
 * context to switch to the proper function to run.
 */
public class OPSFuncImplSignalException extends OPSVMachRuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Creates an instance of the exception.
   * @param fn the name of the fn encountered (for debugging purposes;
   *   don't make this the entire instruction b/c func impl bodies can be
   *   huge).
   */
  public OPSFuncImplSignalException(final String fnName) {
    super(fnName);
  }
}
