/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents an emission of a single PeopleCode
 * instruction.
 */
public class PCInstruction implements IEmission {

  private String instruction;

  /**
   * Creates a new PCInstruction instance.
   * @param i the PeopleCode instruction that was executed
   */
  public PCInstruction(final String i) {
    this.instruction = i;

    // Remove trailing semicolons.
    if (this.instruction.charAt(i.length() - 1) == ';') {
      this.instruction = this.instruction.substring(0,
          this.instruction.length() - 1);
    }
  }

  /**
   * Retrieves the instruction represented by this emission.
   * @return the PeopleCode instruction represented by this
   *   emission.
   */
  public String getInstruction() {
    return this.instruction;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (!(obj instanceof PCInstruction)) {
      return false;
    }

    final PCInstruction other = (PCInstruction) obj;
    if (this.instruction.equals(other.instruction)) {
      return true;
    }

    /*
     * Long emissions in the trace file (i.e., calls to Fill rowsets)
     * are typically cut off around 300 characters, but I've seen variation
     * in this limit. Therefore, if one long instruction starts with the text
     * of the other or vice versa, consider the instructions to be equal.
     */
    final int PC_INSTR_CUTOFF_CHAR_LIMIT = 300;
    if (this.instruction.length() > PC_INSTR_CUTOFF_CHAR_LIMIT
        || other.instruction.length() > PC_INSTR_CUTOFF_CHAR_LIMIT) {
      return (this.instruction.startsWith(other.instruction)
          || other.instruction.startsWith(this.instruction));
    }

    return false;
  }

  @Override
  public int hashCode() {
    final int HBC_INITIAL = 281, HBC_MULTIPLIER = 61;

    return new HashCodeBuilder(HBC_INITIAL,
        HBC_MULTIPLIER).append(this.instruction).toHashCode();
  }

  @Override
  public String toString() {
    return "-: " + this.instruction;
  }
}
