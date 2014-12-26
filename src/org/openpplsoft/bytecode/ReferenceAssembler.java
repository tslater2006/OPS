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

import org.openpplsoft.pt.BytecodeReference;
import org.openpplsoft.pt.peoplecode.PeopleCodeByteStream;

/**
 * Assembles reference instructions into their
 * equivalent textual form.
 */
public class ReferenceAssembler extends ElementAssembler {

  /**
   * Constructs a new reference assembler.
   * @param b the starting byte of the reference bytecode
   *   instruction to assemble
   */
  public ReferenceAssembler(final byte b) {
    this.startByte = b;
    this.formatBitmask = AFlag.SPACE_BEFORE_AND_AFTER;
  }

  @Override
  public void assemble(final PeopleCodeByteStream stream) {

    final int WIDE_AND = 0xff,
              MULTIPLIER_SHIFT = 256,
              CHAINED_REFERENCE_BYTE = 74,
              QUOTED_REFERENCE_BYTE = 72;

    final int b1 = (int) (stream.readNextByte() & WIDE_AND);
    final int b2 = (int) (stream.readNextByte() & WIDE_AND);

    final int refIdx = b2 * MULTIPLIER_SHIFT + b1 + 1;
    final BytecodeReference refObj = stream.getBytecodeReference(refIdx);

    if (refObj == null) {
      throw new OPSAssembleException("No reference is mapped to index "
        + refIdx + " on the program underlying this stream.");
    }

    String ref = refObj.getValue();

    /*
     * If the reference refers to a data buffer or component buffer
     * chained to an expression before it using the dot (".") operator,
     * we need to strip the defn type (Field,Record,Scroll) from the reference.
     */
    if (this.startByte == CHAINED_REFERENCE_BYTE
        && (ref.startsWith("Field.")
        || ref.startsWith("Record.") || ref.startsWith("Scroll."))) {
      ref = ref.substring(ref.indexOf('.') + 1);
    }

    /*
     * This code apparently puts string literals around a definition; i.e,
     * producing: MenuName."ESTABLISH_COURSES" -> why this is I'm not sure,
     * it may be deprecated PeopleCode that still works for backwards
     * compatibility reasons. I'm going to strip the quotes b/c the
     * interpreter expects definition literals without quotes.
     */
    final int p1 = ref.indexOf('.');
    if (this.startByte == (byte) QUOTED_REFERENCE_BYTE && p1 > 0) {
      final String rec = ref.substring(0, p1);
      //ORIGINAL LINE: ref = rec + ".\"" + ref.substring(p1 + 1) + "\"";
      ref = rec.concat("." + ref.substring(p1 + 1));
    }

    /*
     * Before emitting the reference text, emit the reference index.
     * This is required during component loading.
     */
    stream.appendAssembledText("#OPSREF{" + refIdx + "}");
    stream.appendAssembledText(ref);
  }
}
