/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/
/* This file contains modified code derived from the excellent "Decode       *\
|* PeopleCode" open source project, maintained by Erik H                     *|
|* and available under the ISC license at                                    *|
|* http://sourceforge.net/projects/decodepcode/. The associated              *|
|* license text has been reproduced here in accordance with                  *|
|* the license requirements:                                                 *|
|* ------------------------------------------------------------------------- *|
|* Copyright (c)2011 Erik H (erikh3@users.sourceforge.net)                   *|
|*                                                                           *|
|* Permission to use, copy, modify, and/or distribute this software for any  *|
|* purpose with or without fee is hereby granted, provided that the above    *|
|* copyright notice and this permission notice appear in all copies.         *|
|*                                                                           *|
|* THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES  *|
|* WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF          *|
|* MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR   *|
|* ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES    *|
|* WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN     *|
|* ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF   *|
|* OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.            *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.bytecode;

import org.openpplsoft.pt.peoplecode.PeopleCodeByteStream;

/**
 * Assembles numbers from bytecode into text form.
 */
public class NumberAssembler extends ElementAssembler {

  private final int nBytes;

  /**
   * Constructs a new number assembler.
   * @param b the starting byte of the bytecode instruction
   *   to assemble
   * @param n the number of bytes used in the instruction to
   *   represent the numeric value encoded within
   */
  public NumberAssembler(final byte b, final int n) {
    this.startByte = b;
    this.nBytes = n;
    this.formatBitmask = AFlag.SPACE_BEFORE | AFlag.NO_SPACE_AFTER;
  }

  @Override
  public void assemble(final PeopleCodeByteStream stream) {

    final int BYTES_TO_IGNORE = 3,
              SHIFT_MULTIPLIER = 256,
              WIDE_AND = 0xff;

    // decimal position from far right going left
    int dValue = 0;

    String outNumber = "";
    final int numBytes = this.nBytes - BYTES_TO_IGNORE;

    // skip the first byte
    stream.incrementCursor();

    dValue = (int) stream.readNextByte();
    long val = 0, fact = 1;

    for (int i = 0; i < numBytes; i++) {
      val += fact * (long) (stream.readNextByte() & WIDE_AND);
      fact = fact * (long) SHIFT_MULTIPLIER;
    }

    outNumber = Long.toString(val);

    if (dValue > 0 && !outNumber.equals("0")) {

      while (dValue > outNumber.length()) {
        outNumber = "0" + outNumber;
      }

      outNumber = outNumber.substring(0, outNumber.length() - dValue) + "."
          + outNumber.substring(outNumber.length() - dValue);

      if (outNumber.startsWith(".")) {
        outNumber = "0" + outNumber;
      }
    }

    stream.appendAssembledText(outNumber);
  }
}
