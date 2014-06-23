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
 * Assembles embedded strings (string literals).
 */
public class EmbeddedStringAssembler extends PureStringAssembler {

  private String preStr, postStr;

  /**
   * Creates a new embedded string assembler.
   * @param b the starting byte of the bytecode instruction to
   *   be assembled
   * @param pre the character leading the embedded string (i.e., ")
   * @param post the character closing the embedded string (i.e., ")
   */
  public EmbeddedStringAssembler(final byte b, final String pre,
      final String post) {
    this(b, pre, post, AFlag.SPACE_BEFORE_AND_AFTER);
  }

  /**
   * Creates a new embedded string assembler.
   * @param b the starting byte of the bytecode instruction to
   *   be assembled
   * @param pre the character leading the embedded string (i.e., ")
   * @param post the character closing the embedded string (i.e., ")
   * @param format the format bit mask to be used when assembling the
   *   instruction into its equivalent textual form
   */
  public EmbeddedStringAssembler(final byte b, final String pre,
      final String post, final int format) {
    super(b);
    this.preStr = pre;
    this.postStr = post;
    this.formatBitmask = format;
  }

  @Override
  public void assemble(final PeopleCodeByteStream stream) {
    stream.appendAssembledText(this.preStr
        + getString(stream).replace("\"", "\"\"") + this.postStr);
  }
}
