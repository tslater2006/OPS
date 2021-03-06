/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt.peoplecode;

import org.openpplsoft.pt.*;

public class PeopleCodeByteStream {

  private final PeopleCodeProg prog;
  private final StringBuilder assembledTextBuilder;

  private int cursorPos;

  public PeopleCodeByteStream(PeopleCodeProg prog) {
    this.prog = prog;
    this.assembledTextBuilder = new StringBuilder();
  }

  public PeopleCodeProg getProg() {
    return this.prog;
  }

  public void setCursorPos(int pos) {
    this.cursorPos = pos;
  }

  public void incrementCursor() {
    this.cursorPos++;
  }

  public void decrementCursor() {
    this.cursorPos--;
  }

  public int getCursorPos() {
    return cursorPos;
  }

  public int getProgLenInBytes() {
    return this.prog.getBytecode().length;
  }

  public byte readNextByte() {
    return prog.getBytecode()[this.cursorPos++];
  }

  public byte readAhead() {
    if(this.cursorPos >= this.prog.getBytecode().length - 1) {
      return -1;
    }
    return prog.getBytecode()[this.cursorPos];
  }

  public BytecodeReference getBytecodeReference(int idx) {
    return this.prog.getBytecodeReference(idx);
  }

  public void appendAssembledText(char c) {
        this.assembledTextBuilder.append(c);
  }

  public void appendAssembledText(String s) {
    this.assembledTextBuilder.append(s);
  }

  public String getAssembledText() {
    return this.assembledTextBuilder.toString();
  }
}
