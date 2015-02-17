/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents an emission indicating the start of execution
 * of a PeopleCode method, function, or other sub-program unit
 * of logic/control.
 */
public class PCStart implements IEmission {

  private final String startTag, nest, methodName, progDescriptor;

  /**
   * Creates a new PCStart instance.
   * @param s the start-tag for the emission
   * @param n the nesting level of the execution demarcated
   *   by this emission
   * @param m the method being executed
   * @param p the parent program of the method being
   *   executed
   */
  public PCStart(final String s, final String n,
      final String m, final String p) {
    this.startTag = s;
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
    } else if (!(obj instanceof PCStart)) {
      return false;
    }

    final PCStart other = (PCStart) obj;
    if (this.startTag.equals(other.startTag)
        && this.nest.equals(other.nest)
        && this.methodName.equals(other.methodName)
        && this.progDescriptor.equals(other.progDescriptor)) {
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 103, HCB_MULTIPLIER = 277;

    final HashCodeBuilder hbc = new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.startTag).append(this.nest)
        .append(this.methodName).append(this.progDescriptor);

    return hbc.toHashCode();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append(">>> ").append(this.startTag);
    builder.append("     Nest=").append(this.nest);
    builder.append(' ').append(this.methodName);
    builder.append(' ').append(this.progDescriptor);
    return builder.toString();
  }
}
