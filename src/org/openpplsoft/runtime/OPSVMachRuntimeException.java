/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import java.lang.RuntimeException;

public class OPSVMachRuntimeException extends RuntimeException {

  public OPSVMachRuntimeException(String msg) {
    super(msg);
  }

  public OPSVMachRuntimeException(String msg, Exception ex) {
    super(msg, ex);
  }
}
