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
 * Parent class for core elemental parts of a PeopleCode
 * program's byte stream (i.e., comments, numbers, strings, etc).
 */
public abstract class ElementAssembler {

  protected byte startByte;
  protected int formatBitmask;

  /**
   * All subclasses must implement this method
   * with logic to assemble their corresponding bytecode
   * instruction into its textual equivalent.
   * @param stream the PeopleCode bytecode stream to assemble from
   */
  public abstract void assemble(PeopleCodeByteStream stream);

  /**
   * Returns the start byte for the bytecode instructions this
   * assembler is tasked with assembling.
   * @return the start byte
   */
  public byte getStartByte() {
    return this.startByte;
  }

  /**
   * Returns whether or not this assembler writes non-blank
   * characters (default is true for all assemblers, must
   * be overriden by subclasses to specify otherwise).
   * @return whether or not this assembler writes non-blank
   * characters.
   */
  public boolean writesNonBlank() {
    return true;
  }
}

