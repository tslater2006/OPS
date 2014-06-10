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

package com.enterrupt.bytecode;

public class AFlag {

  public static final int PUNCTUATION = 0x0;
  public static final int SPACE_BEFORE = 0x1;
  public static final int SPACE_AFTER = 0x2;
  public static final int NEWLINE_BEFORE = 0x4;
  public static final int NEWLINE_AFTER = 0x8;
  public static final int INCREASE_INDENT = 0x10;
  public static final int DECREASE_INDENT = 0x20;
  public static final int RESET_INDENT_BEFORE = 0x80;
  public static final int RESET_INDENT_AFTER = 0x100;
  public static final int NO_SPACE_BEFORE = 0x200;
  public static final int NO_SPACE_AFTER = 0x400;
  public static final int INCREASE_INDENT_ONCE = 0x800;
  public static final int AND_INDICATOR = 0x1000;
  public static final int NEWLINE_ONCE = 0x2000;
  public static final int IN_DECLARE = 0x4000;
  public static final int SEMICOLON = 0x8000;
  public static final int SPACE_BEFORE2 = 0x10000;
  public static final int COMMENT_ON_SAME_LINE = 0x20000;
  public static final int R_BRACKET = 0x80000;
  public static final int L_BRACKET = 0x40000;
  public static final int SPACE_BEFORE_AND_AFTER = SPACE_BEFORE | SPACE_AFTER;
  public static final int SPACE_BEFORE_AND_AFTER2 = SPACE_BEFORE2 | SPACE_BEFORE | SPACE_AFTER;
  public static final int AND_STYLE = NEWLINE_AFTER | SPACE_BEFORE2 | SPACE_BEFORE | AND_INDICATOR;
  public static final int FOR_STYLE = NEWLINE_BEFORE | SPACE_AFTER | INCREASE_INDENT;
  public static final int IF_STYLE = NEWLINE_BEFORE | SPACE_BEFORE | SPACE_AFTER;
  public static final int NEWLINE_BEFORE_AND_AFTER = NEWLINE_BEFORE | NEWLINE_AFTER;
  public static final int NEWLINE_BEFORE_SPACE_AFTER = NEWLINE_BEFORE | SPACE_AFTER;
  public static final int SPACE_BEFORE_NEWLINE_AFTER = SPACE_BEFORE | NEWLINE_AFTER;
  public static final int THEN_STYLE = SPACE_BEFORE | SPACE_BEFORE2 | NEWLINE_AFTER |
      SPACE_AFTER | INCREASE_INDENT;
  public static final int ELSE_STYLE = NEWLINE_BEFORE | DECREASE_INDENT | NEWLINE_AFTER | INCREASE_INDENT;
  public static final int ENDIF_STYLE = NEWLINE_BEFORE | SPACE_BEFORE | DECREASE_INDENT | NEWLINE_AFTER;
  public static final int FUNCTION_STYLE = NEWLINE_BEFORE | SPACE_AFTER | INCREASE_INDENT | RESET_INDENT_BEFORE;
  public static final int END_FUNCTION_STYLE = NEWLINE_BEFORE | RESET_INDENT_BEFORE;
}
