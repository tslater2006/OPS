/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.antlr4;

import org.openpplsoft.runtime.OPSVMachRuntimeException;

public class EntReturnException extends OPSVMachRuntimeException {

  public EntReturnException(String instructionText) {
    super(instructionText);
  }
}
