/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents the demarcation in a PS tracefile
 * signaling the end of execution of a PeopleCode program,
 * method, function, transfer of control, etc.
 */
public class PCEnd implements IEmission {

  private String endTag;
  private String nest;
  private String methodName;
  private String progDescriptor;

  /**
   * Creates a new instance of PCEnd.
   * @param s the end-tag in the tracefile
   * @param n the nesting level at which execution was
   *    occurring.
   * @param m the method name that was being executed
   * @param p the program that was being executed
   */
  public PCEnd(final String s, final String n,
      final String m, final String p) {
    this.endTag = s;
    this.nest = n;
    this.methodName = m;
    this.progDescriptor = p;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof PCEnd)) {
      return false;
    }

    final PCEnd other = (PCEnd) obj;
    if (this.endTag.equals(other.endTag)
        && this.nest.equals(other.nest)
        && this.methodName.equals(other.methodName)
        && this.progDescriptor.equals(other.progDescriptor)) {
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 433, HCB_MULTIPLIER = 83;

    final HashCodeBuilder hbc = new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.endTag).append(this.nest)
        .append(this.methodName).append(this.progDescriptor);

    return hbc.toHashCode();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append(">>> ").append(this.endTag);
    builder.append("     Nest=").append(this.nest);
    builder.append(" ").append(this.methodName);
    builder.append(" ").append(this.progDescriptor);
    return builder.toString();
  }
}
