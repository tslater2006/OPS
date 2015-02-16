/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt.peoplecode;

import java.sql.*;
import org.openpplsoft.sql.StmtLibrary;
import org.openpplsoft.pt.*;

public class RecordPeopleCodeProg extends PeopleCodeProg {

  private final String RECNAME, FLDNAME;

  public RecordPeopleCodeProg(final String recname,
      final String fldname, final String event) {
    super(event);
    this.RECNAME = recname;
    this.FLDNAME = fldname;
    this.initBindVals();
  }

  protected void initBindVals() {
    this.bindVals = new String[14];
    this.bindVals[0] = PSDefn.RECORD;
    this.bindVals[1] = this.RECNAME;
    this.bindVals[2] = PSDefn.FIELD;
    this.bindVals[3] = this.FLDNAME;
    this.bindVals[4] = PSDefn.EVENT;
    this.bindVals[5] = this.event;
    for(int i = 6; i < this.bindVals.length; i+=2) {
      this.bindVals[i] = "0";
      this.bindVals[i+1] = PSDefn.NULL;
    }
  }

  public String getDescriptor() {
    return "RecordPC." + this.RECNAME + "." + this.FLDNAME + "." + this.event;
  }
}
