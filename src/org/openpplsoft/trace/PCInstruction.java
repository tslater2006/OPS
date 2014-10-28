/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.trace;

import org.openpplsoft.runtime.OPSVMachRuntimeException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents an emission of a single PeopleCode
 * instruction.
 */
public class PCInstruction implements IEmission {

  private static Logger log =
      LogManager.getLogger(PCInstruction.class.getName());

  private String instruction;

  // These are used in determining which instructions should be
  // emitted; only used by OPS-constructed PCInstruction objects,
  // not PCInstruction objects created from the tracefile.
  public Token sourceToken;
  public ParserRuleContext sourceContext;

  /**
   * Creates a new PCInstruction instance.
   * @param i the PeopleCode instruction that was executed
   */
  public PCInstruction(final String i) {
    this.instruction = i;

    // Although the interpreter will never emit an instruction emission
    // containing a comment, the tracefile includes comments that appear
    // on the same line as PC instructions. Therefore, if there is a trailing
    // comment on this instruction, it should be removed to ensure that
    // tracefile verification doesn't fail for otherwise equivalent emissions.
    Pattern commPattern = Pattern.compile("\\s+/\\*(.+?)\\*/\\s*$");
    Matcher commMatcher = commPattern.matcher(this.instruction);
    if (commMatcher.find()) {
      this.instruction = commMatcher.replaceAll("");
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

  public boolean isOptional() {
    return this.instruction.startsWith("End-If")
        || this.instruction.startsWith("Else")
        || this.instruction.startsWith("Break")
        || this.instruction.startsWith("End-Evaluate")
        || this.instruction.startsWith("End-For");
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
    final int HCB_INITIAL = 281, HCB_MULTIPLIER = 61;

    return new HashCodeBuilder(HCB_INITIAL,
        HCB_MULTIPLIER).append(this.instruction).toHashCode();
  }

  @Override
  public String toString() {
    return "-: " + this.instruction;
  }
}
