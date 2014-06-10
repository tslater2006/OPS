/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.antlr4;

import org.openpplsoft.runtime.EntVMachRuntimeException;

public class EntInterpretException extends EntVMachRuntimeException {

  public EntInterpretException(String msg, String input, int lineNbr) {
    super(msg + "; input: \"" + input + "\" on line " + lineNbr);
  }
}
