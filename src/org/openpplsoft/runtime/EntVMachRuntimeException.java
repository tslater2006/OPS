/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package com.enterrupt.runtime;

import java.lang.RuntimeException;

public class EntVMachRuntimeException extends RuntimeException {

  public EntVMachRuntimeException(String msg) {
    super(msg);
  }

  public EntVMachRuntimeException(String msg, Exception ex) {
    super(msg, ex);
  }
}
