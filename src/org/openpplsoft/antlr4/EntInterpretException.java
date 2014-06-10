/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package com.enterrupt.antlr4;

import com.enterrupt.runtime.EntVMachRuntimeException;

public class EntInterpretException extends EntVMachRuntimeException {

  public EntInterpretException(String msg, String input, int lineNbr) {
    super(msg + "; input: \"" + input + "\" on line " + lineNbr);
  }
}
