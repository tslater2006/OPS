/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.antlr4;

import org.openpplsoft.runtime.OPSVMachRuntimeException;

/**
 * This exception is thrown by the interpreter when a Break
 * stmt is encountered; the exception will subsequently
 * be caught by various constructs (For, Evaluate, etc)
 * that have interruptible control flow semantics, which will
 * handle the break and any related cleanup/processing accordingly.
 */
public class OPSBreakSignalException extends OPSVMachRuntimeException {

  /**
   * Creates an instance of the exception.
   */
  public OPSBreakSignalException() {
    super("Break");
  }
}
