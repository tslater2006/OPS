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
 * Assembles simple elements (i.e., "<", "And", etc.).
 */
public class SimpleElementAssembler extends ElementAssembler {

  private String text;

  /**
   * Creates a new simple element assembler without
   * a format bitmask explicitly defined.
   * @param b the starting byte of the bytecode instruction
   *   to assemble
   * @param t the text equivalent of the starting byte
   */
  public SimpleElementAssembler(final byte b, final String t) {
    this(b, t, AFlag.SPACE_BEFORE_AND_AFTER);
  }

  /**
   * Creates a new simple element assembler with a format
   * bitmask.
   * @param b the starting byte of the bytecode instruction to
   *   assemble
   * @param t the text equivalent of the starting byte
   * @param f the format bitmask to use during assembly
   */
  public SimpleElementAssembler(final byte b, final String t,
      final int f) {
    this.startByte = b;
    this.text = t;
    this.formatBitmask = f;
  }

  @Override
  public void assemble(final PeopleCodeByteStream stream) {
    stream.appendAssembledText(t);
  }

  @Override
  public boolean writesNonBlank() {
    return t.trim().length() > 0;
  }
}
