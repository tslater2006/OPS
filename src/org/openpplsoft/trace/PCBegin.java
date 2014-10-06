/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents the demarcation emission in PS
 * tracefiles signaling the start of execution
 * of a PeopleCode program.
 */
public class PCBegin implements IEmission {

  private String progDescriptor;
  private int level, row;

  /**
   * Creates a new PCBegin emission object.
   * @param pd the program descriptor from the tracefile
   *   naming the program that is executed in suceeding lines
   * @param l the nesting level at which execution is
   *   ocurring.
   * @param r the row number for the emission
   */
  public PCBegin(final String pd, final int level,
      final int row) {
    this.progDescriptor = pd;
    this.level = level;
    this.row = row;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof PCBegin)) {
      return false;
    }

    final PCBegin other = (PCBegin) obj;
    if (this.progDescriptor.equals(other.progDescriptor)
        && this.level == other.level && this.row == other.row) {
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int HCB_INITIAL = 17, HCB_MULTIPLIER = 37;

    final HashCodeBuilder hbc = new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.progDescriptor)
        .append(this.level).append(this.row);

    return hbc.toHashCode();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append(">>>>> Begin ").append(this.progDescriptor);
    builder.append(" level ").append(this.level);
    builder.append(" row ").append(this.row);
    return builder.toString();
  }
}
