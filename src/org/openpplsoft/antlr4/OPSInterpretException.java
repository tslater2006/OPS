/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.antlr4;

import org.openpplsoft.runtime.OPSVMachRuntimeException;

/**
 * OPS runtime exception meant exclusively for
 * exceptional events occurring during execution of the
 * interpreter.
 */
public class OPSInterpretException extends OPSVMachRuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs an exception message from the provided args.
   * @param msg the msg describing the exceptional situation
   * @param input the input that caused the interpreter to throw the
   *    exception
   * @param lineNbr the line number in the PeopleCode program where
   *    the input is located
   */
  public OPSInterpretException(final String msg, final String input,
      final int lineNbr) {
    super(msg + "; input: \"" + input + "\" on line " + lineNbr);
  }
}
