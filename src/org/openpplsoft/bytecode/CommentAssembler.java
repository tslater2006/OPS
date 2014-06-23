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
 * Assembles PeopleCode comments from bytecode into their textual
 * equivalent.
 */
public class CommentAssembler extends ElementAssembler {

  /**
   * Creates a new instance of CommentAssembler.
   * @param byteMarker the byte marker that this assembler
   *   will be tasked with assembling.
   */
  public CommentAssembler(final byte byteMarker) {
    this(byteMarker, AFlag.NEWLINE_BEFORE_AND_AFTER);
  }

  /**
   * Creates a new instance of CommentAssembler.
   * @param b the byte marker that this assembler
   *    will be tasked with assembling.
   * @param f the format bitmask to use
   *    during assembly of instructions from the byte
   *    stream
   */
  public CommentAssembler(final byte b,
      final int f) {
    this.startByte = b;
    this.formatBitmask = f;
  }

  /**
   * Assembles the imminent comment instruction in the byte stream
   * into its textual equivalent.
   * @param stream a bytecode stream that has a cursor positioned
   *    on an instruction beginning with byte {@code getStartByte()}.
   */
  public void assemble(final PeopleCodeByteStream stream) {

    final int WIDE_AND = 0xff,
              NEWLINE_MARKER = 10,
              COMM_LEN_BYTE2_MULTIPLIER = 256;

    // Length byte is wide ANDed and cast to integer.
    int commLen = ((int) stream.readNextByte()) & WIDE_AND;
    commLen = commLen + ((int) stream.readNextByte() & WIDE_AND)
        * COMM_LEN_BYTE2_MULTIPLIER;

    byte accumByte;
    for (int i = 0; i < commLen
        && (stream.getCursorPos() < stream.getProgLenInBytes()); i++) {
      accumByte = stream.readNextByte();
      if (accumByte != 0) {
        if (accumByte == (byte) NEWLINE_MARKER) {
          stream.appendAssembledText('\n');
        } else {
          stream.appendAssembledText((char) accumByte);
        }
      }
    }
  }

  @Override
  public boolean writesNonBlank() {
    return true;
  }
}
