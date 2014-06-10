/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package com.enterrupt.trace;

public class PCInstruction implements IEmission {

  public String instruction;

  public PCInstruction(String i) {
    this.instruction = i;

    // Remove trailing semicolons.
    if(this.instruction.charAt(i.length() - 1) == ';') {
      this.instruction = this.instruction.substring(0,
        this.instruction.length() - 1);
    }
  }

  public boolean equals(Object obj) {
    if(obj == this)
      return true;
    if(obj == null)
      return false;
    if(!(obj instanceof PCInstruction))
      return false;

    PCInstruction other = (PCInstruction)obj;
    if(this.instruction.equals(other.instruction)) {
      return true;
    }

    /*
     * Long emissions in the trace file (i.e., calls to Fill rowsets)
     * are typically cut off around 300 characters, but I've seen variation
     * in this limit. Therefore, if one long instruction starts with the text
     * of the other or vice versa, consider the instructions to be equal.
     */
    if(this.instruction.length() > 300 || other.instruction.length() > 300) {
      return (this.instruction.startsWith(other.instruction) ||
          other.instruction.startsWith(this.instruction));
    }
    return false;
  }

  public String toString() {
    return "-: " + this.instruction;
  }
}
