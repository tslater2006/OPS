/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import org.openpplsoft.runtime.OPSVMachRuntimeException;

public class OPSDataTypeException extends OPSVMachRuntimeException {

  public OPSDataTypeException(final String msg) {
    super(msg);
  }

  public OPSDataTypeException(final String msg, final Exception ex) {
    super(msg, ex);
  }
}
