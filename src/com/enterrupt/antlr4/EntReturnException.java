/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package com.enterrupt.antlr4;

import com.enterrupt.runtime.EntVMachRuntimeException;

public class EntReturnException extends EntVMachRuntimeException {

  public EntReturnException(String instructionText) {
    super(instructionText);
  }
}
