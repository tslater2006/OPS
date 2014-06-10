/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package com.enterrupt.types;

import com.enterrupt.runtime.EntVMachRuntimeException;

public class EntDataTypeException extends EntVMachRuntimeException {

  public EntDataTypeException(String msg) {
    super(msg);
  }
}
