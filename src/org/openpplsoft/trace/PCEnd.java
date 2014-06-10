/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

public class PCEnd implements IEmission {

  public String endTag;
  public String nest;
  public String methodName;
  public String progDescriptor;

  public PCEnd(String s, String n, String m, String p) {
    this.endTag = s;
    this.nest = n;
    this.methodName = m;
    this.progDescriptor = p;
  }

  public boolean equals(Object obj) {
    if(obj == this)
      return true;
    if(obj == null)
      return false;
    if(!(obj instanceof PCEnd))
      return false;

    PCEnd other = (PCEnd)obj;
    if(this.endTag.equals(other.endTag)
      && this.nest.equals(other.nest)
      && this.methodName.equals(other.methodName)
      && this.progDescriptor.equals(other.progDescriptor)) {
      return true;
    }
    return false;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(">>> ").append(this.endTag);
    builder.append("     Nest=").append(this.nest);
    builder.append(" ").append(this.methodName);
    builder.append(" ").append(this.progDescriptor);
    return builder.toString();
  }
}
