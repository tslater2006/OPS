/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.runtime.EntVMachRuntimeException;

public class EntDataTypeException extends EntVMachRuntimeException {

  public EntDataTypeException(String msg) {
    super(msg);
  }
}
