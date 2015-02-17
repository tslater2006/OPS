/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt.peoplecode;

import java.lang.StringBuilder;
import org.openpplsoft.pt.*;

public class PagePeopleCodeProg extends PeopleCodeProg {

  private String PNLNAME;

  public PagePeopleCodeProg(final String pnlname) {
    super("Activate");
    this.PNLNAME = pnlname;
    this.initBindVals();
  }

  protected void initBindVals() {
    this.bindVals = new String[14];

    this.bindVals[0] = PSDefn.PAGE;
    this.bindVals[1] = this.PNLNAME;
    this.bindVals[2] = PSDefn.EVENT;
    this.bindVals[3] = this.event;
    for(int i = 4; i < this.bindVals.length; i+=2) {
      this.bindVals[i] = "0";
      this.bindVals[i+1] = PSDefn.NULL;
    }
  }

  public String getDescriptor() {
    StringBuilder builder = new StringBuilder();
    builder.append("PagePC.").append(this.PNLNAME).append('.').append(this.event);
    return builder.toString();
  }
}
