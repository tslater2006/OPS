/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.runtime;

public class OPSIllegalNonCBufferFieldAccessAttempt
    extends OPSVMachRuntimeException {

  private final String RECNAME, FIELDNAME;

  public OPSIllegalNonCBufferFieldAccessAttempt(
      final String RECNAME, final String FIELDNAME) {
    super();
    this.RECNAME = RECNAME;
    this.FIELDNAME = FIELDNAME;
  }

  public String getRecName() {
    return this.RECNAME;
  }

  public String getFieldName() {
    return this.FIELDNAME;
  }
}
