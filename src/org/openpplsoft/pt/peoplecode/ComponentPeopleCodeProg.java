/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt.peoplecode;

import org.openpplsoft.pt.*;

public class ComponentPeopleCodeProg extends PeopleCodeProg {

  private final String PNLGRPNAME, MARKET, RECNAME, FLDNAME;

  public ComponentPeopleCodeProg(
      final String pnlgrpname,
      final String market,
      final String event) {
    super(event);
    this.PNLGRPNAME = pnlgrpname;
    this.MARKET = market;
    this.RECNAME = null;
    this.FLDNAME = null;
    this.initBindVals();
  }

  public ComponentPeopleCodeProg(
      final String pnlgrpname,
      final String market,
      final String recname,
      final String event) {
    super(event);
    this.PNLGRPNAME = pnlgrpname;
    this.MARKET = market;
    this.RECNAME = recname;
    this.FLDNAME = null;
    this.initBindVals();
  }

  public ComponentPeopleCodeProg(
      final String pnlgrpname,
      final String market,
      final String recname,
      final String fldname,
      final String event) {
    super(event);
    this.PNLGRPNAME = pnlgrpname;
    this.MARKET = market;
    this.RECNAME = recname;
    this.FLDNAME = fldname;
    this.initBindVals();
  }

  protected void initBindVals() {

    this.bindVals = new String[14];

    this.bindVals[0] = PSDefn.COMPONENT;
    this.bindVals[1] = this.PNLGRPNAME;
    this.bindVals[2] = PSDefn.MARKET;
    this.bindVals[3] = this.MARKET;
    for(int i = 4; i < this.bindVals.length; i+=2) {
      this.bindVals[i] = "0";
      this.bindVals[i+1] = PSDefn.NULL;
    }

    int idx = 4;
    if(this.RECNAME != null) {
      this.bindVals[idx++] = PSDefn.RECORD;
      this.bindVals[idx++] = this.RECNAME;

      if(this.FLDNAME != null) {
        this.bindVals[idx++] = PSDefn.FIELD;
        this.bindVals[idx++] = this.FLDNAME;
      }
    }

    this.bindVals[idx++] = PSDefn.EVENT;
    this.bindVals[idx++] = this.event;
  }

  public String getDescriptor() {

    StringBuilder builder = new StringBuilder();
    builder.append("ComponentPC.").append(this.PNLGRPNAME).append('.').append(this.MARKET).append('.');

    if(this.RECNAME != null) {
      builder.append(this.RECNAME).append('.');

      if(this.FLDNAME != null) {
        builder.append(this.FLDNAME).append('.');
      }
    }

    builder.append(this.event);
    return builder.toString();
  }

  public String getRecName() {
    return this.RECNAME;
  }

  public String getFldName() {
    return this.FLDNAME;
  }
}
