/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

public class PCBegin implements IEmission {

  public String progDescriptor;
  public String level;
  public String row;

  public PCBegin(String pd, String l, String r) {
    this.progDescriptor = pd;
    this.level = l;
    this.row = r;
  }

  public boolean equals(Object obj) {
    if(obj == this)
      return true;
    if(obj == null)
      return false;
    if(!(obj instanceof PCBegin))
      return false;

    PCBegin other = (PCBegin)obj;
    if(this.progDescriptor.equals(other.progDescriptor) &&
      this.level.equals(other.level) && this.row.equals(other.row)) {
      return true;
    }
    return false;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(">>>>> Begin ").append(this.progDescriptor);
    builder.append(" level ").append(this.level);
    builder.append(" row ").append(this.row);
    return builder.toString();
  }
}
