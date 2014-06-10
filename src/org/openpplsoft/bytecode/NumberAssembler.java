/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
|*===---------------------------------------------------------------------===*|
|* This file contains modified code derived from the excellent "Decode       *|
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

public class NumberAssembler extends ElementAssembler {

  byte b;
  int nBytes;

  public NumberAssembler(byte _b, int _nBytes) {
    b = _b;
    nBytes = _nBytes;
    format = AFlag.SPACE_BEFORE | AFlag.NO_SPACE_AFTER;
  }

  public byte getStartByte() {
    return b;
  }

  public void assemble(PeopleCodeByteStream stream) {

    int dValue = 0;  // decimal position from far right going left
    String out_number = "";
    int num_bytes = nBytes - 3;

    stream.incrementCursor(); // skip first byte
    dValue = (int) stream.readNextByte();
    long val = 0, fact = 1;

    for(int i = 0; i < num_bytes; i++) {
      val += fact * (long) (stream.readNextByte() & 0xff);
      fact = fact * (long) 256;
    }

    out_number = Long.toString(val);

    if(dValue > 0 && !out_number.equals("0")) {

      while(dValue > out_number.length()) {
        out_number = "0" + out_number;
      }
      out_number = out_number.substring(0, out_number.length() - dValue) + "." +
             out_number.substring(out_number.length() - dValue);

      if(out_number.startsWith(".")) {
        out_number = "0" + out_number;
      }
    }

    stream.appendAssembledText(out_number);
  }
}
