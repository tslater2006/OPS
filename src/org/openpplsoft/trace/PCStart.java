/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

public class PCStart implements IEmission {

  public String startTag;
  public String nest;
  public String methodName;
  public String progDescriptor;

  public PCStart(String s, String n, String m, String p) {
    this.startTag = s;
    this.nest = n;
    this.methodName = m;
    this.progDescriptor = p;
  }

  public boolean equals(Object obj) {
    if(obj == this)
      return true;
    if(obj == null)
      return false;
    if(!(obj instanceof PCStart))
      return false;

    PCStart other = (PCStart)obj;
    if(this.startTag.equals(other.startTag)
      && this.nest.equals(other.nest)
      && this.methodName.equals(other.methodName)
      && this.progDescriptor.equals(other.progDescriptor)) {
      return true;
    }
    return false;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(">>> ").append(this.startTag);
    builder.append("     Nest=").append(this.nest);
    builder.append(" ").append(this.methodName);
    builder.append(" ").append(this.progDescriptor);
    return builder.toString();
  }
}
