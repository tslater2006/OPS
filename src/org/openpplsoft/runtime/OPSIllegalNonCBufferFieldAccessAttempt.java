/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

import java.lang.RuntimeException;

public class OPSIllegalNonCBufferFieldAccessAttempt extends OPSVMachRuntimeException {

  public String RECNAME, FIELDNAME;

  public OPSIllegalNonCBufferFieldAccessAttempt(final String RECNAME, final String FIELDNAME) {
    super();
    this.RECNAME = RECNAME;
    this.FIELDNAME = FIELDNAME;
  }
}