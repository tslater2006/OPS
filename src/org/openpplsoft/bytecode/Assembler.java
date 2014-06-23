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

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.peoplecode.PeopleCodeByteStream;
import org.openpplsoft.sql.StmtLibrary;

/**
 * Responsible for driving the assembly process for
 * a particular PeopleCodeByteStream.
 */
@SuppressWarnings("checkstyle:magicnumber")
public class Assembler {

  private static Logger log = LogManager.getLogger(Assembler.class.getName());

  private static ElementAssembler[] allAssemblers;
  private static HashMap<Byte, ElementAssembler> assemblerTable;

  private PeopleCodeByteStream stream;
  private int nIndent;
  private boolean endDetected;
  private boolean firstLine = true;
  private boolean isInDeclare;
  private boolean startOfLine = true;
  private boolean andIndicator;
  private boolean didNewline;
  private boolean wroteSpace;
  private ElementAssembler lastAssembler;

  static {
    // Array of all available assemblers.
    allAssemblers = new ElementAssembler[] {
      new IdentifierAssembler((byte) 0),
      new PureStringAssembler((byte) 1),
      new SimpleElementAssembler((byte) 3, ",",
          AFlag.NO_SPACE_BEFORE | AFlag.SPACE_AFTER),
      new SimpleElementAssembler((byte) 4, "/"),
      new SimpleElementAssembler((byte) 5, ".", AFlag.PUNCTUATION),
      new SimpleElementAssembler((byte) 6, "="),
      new SimpleElementAssembler((byte) 8, ">="),
      new SimpleElementAssembler((byte) 9, ">"),
      // 0x0A : (Function | Method | External Datatype | Class name)
      new PureStringAssembler((byte) 10),
      new SimpleElementAssembler((byte) 11, "(",
          AFlag.NO_SPACE_AFTER),
      new SimpleElementAssembler((byte) 12, "<="),
      new SimpleElementAssembler((byte) 13, "<"),
      new SimpleElementAssembler((byte) 14, "-"),
      new SimpleElementAssembler((byte) 15, "*"),
      new SimpleElementAssembler((byte) 16, "<>"),
      new NumberAssembler((byte) 17, 14),
      // 0x12 : system variable name
      new PureStringAssembler((byte) 18),
      new SimpleElementAssembler((byte) 19, "+"),
      new SimpleElementAssembler((byte) 20, ")",
          AFlag.NO_SPACE_BEFORE),
      new SimpleElementAssembler((byte) 21, ";",
          AFlag.SEMICOLON | AFlag.NEWLINE_AFTER | AFlag.NO_SPACE_BEFORE),
      new EmbeddedStringAssembler((byte) 22, "\"", "\""),
      new SimpleElementAssembler((byte) 24, "And", AFlag.AND_STYLE),
      new SimpleElementAssembler((byte) 25, "Else", AFlag.ELSE_STYLE),
      new SimpleElementAssembler((byte) 26, "End-If", AFlag.ENDIF_STYLE),
      new SimpleElementAssembler((byte) 27, "Error"),
      new SimpleElementAssembler((byte) 28, "If", AFlag.IF_STYLE),
      new SimpleElementAssembler((byte) 29, "Not"),
      new SimpleElementAssembler((byte) 30, "Or", AFlag.AND_STYLE),
      new SimpleElementAssembler((byte) 31, "Then", AFlag.THEN_STYLE),
      new SimpleElementAssembler((byte) 32, "Warning"),
      new ReferenceAssembler((byte) 33),
      new SimpleElementAssembler((byte) 35, "|"),
      new CommentAssembler((byte) 36),
      new SimpleElementAssembler((byte) 37, "While", AFlag.FOR_STYLE),
      new SimpleElementAssembler((byte) 38, "End-While", AFlag.ENDIF_STYLE),
      new SimpleElementAssembler((byte) 41, "For", AFlag.FOR_STYLE),
      new SimpleElementAssembler((byte) 42, "To"),
      new SimpleElementAssembler((byte) 43, "Step"),
      new SimpleElementAssembler((byte) 44, "End-For", AFlag.ENDIF_STYLE),
      new SimpleElementAssembler((byte) 45, "", AFlag.NEWLINE_ONCE),
      new SimpleElementAssembler((byte) 46, "Break", AFlag.SPACE_BEFORE),
      new SimpleElementAssembler((byte) 47, "True",
          AFlag.SPACE_BEFORE_AND_AFTER2),
      new SimpleElementAssembler((byte) 48, "False",
          AFlag.SPACE_BEFORE_AND_AFTER2),
      new SimpleElementAssembler((byte) 49, "Declare",
          AFlag.NEWLINE_BEFORE_SPACE_AFTER | AFlag.IN_DECLARE),
      new SimpleElementAssembler((byte) 50, "Function",
          AFlag.FUNCTION_STYLE),
      new SimpleElementAssembler((byte) 53, "As"),
      new SimpleElementAssembler((byte) 55, "End-Function",
          AFlag.END_FUNCTION_STYLE),
      new SimpleElementAssembler((byte) 56, "Return"),
      new SimpleElementAssembler((byte) 57, "Returns"),
      new SimpleElementAssembler((byte) 58, "PeopleCode"),
      new SimpleElementAssembler((byte) 60, "Evaluate",
          AFlag.INCREASE_INDENT | AFlag.SPACE_AFTER),
      new SimpleElementAssembler((byte) 61, "When",
          AFlag.DECREASE_INDENT | AFlag.NEWLINE_BEFORE_SPACE_AFTER
            | AFlag.INCREASE_INDENT),
      new SimpleElementAssembler((byte) 62, "When-Other",
          AFlag.DECREASE_INDENT | AFlag.NEWLINE_BEFORE
            | AFlag.NEWLINE_AFTER | AFlag.INCREASE_INDENT),
      new SimpleElementAssembler((byte) 63, "End-Evaluate",
          AFlag.NEWLINE_BEFORE | AFlag.DECREASE_INDENT | AFlag.SPACE_BEFORE),
      // 0x40 : PeopleCode variable type name
      new PureStringAssembler((byte) 64),
      new SimpleElementAssembler((byte) 65, "", AFlag.SPACE_AFTER),
      new SimpleElementAssembler((byte) 66, "", AFlag.PUNCTUATION),
      new SimpleElementAssembler((byte) 67, "Exit"),
      new SimpleElementAssembler((byte) 68, "Local",
          AFlag.NEWLINE_BEFORE_SPACE_AFTER),
      new SimpleElementAssembler((byte) 69, "Global",
          AFlag.NEWLINE_BEFORE_SPACE_AFTER),
      new SimpleElementAssembler((byte) 71, "@",
          AFlag.SPACE_BEFORE | AFlag.NO_SPACE_AFTER),
      new ReferenceAssembler((byte) 72),
      new SimpleElementAssembler((byte) 73, "set"),
      new ReferenceAssembler((byte) 74),
      new SimpleElementAssembler((byte) 75, "Null"),
      new SimpleElementAssembler((byte) 76, "[",
          AFlag.L_BRACKET | AFlag.SPACE_BEFORE | AFlag.NO_SPACE_AFTER),
      new SimpleElementAssembler((byte) 77, "]",
          AFlag.R_BRACKET | AFlag.NO_SPACE_BEFORE | AFlag.SPACE_AFTER),
      new CommentAssembler((byte) 78, AFlag.NEWLINE_AFTER
          | AFlag.COMMENT_ON_SAME_LINE | AFlag.SPACE_BEFORE),
      new SimpleElementAssembler((byte) 79, "", AFlag.NEWLINE_AFTER),
      new NumberAssembler((byte) 80, 18),
      new SimpleElementAssembler((byte) 84, "Component",
          AFlag.NEWLINE_BEFORE_SPACE_AFTER),
      new CommentAssembler((byte) 85, AFlag.NEWLINE_AFTER
          | AFlag.COMMENT_ON_SAME_LINE | AFlag.SPACE_BEFORE),
      new SimpleElementAssembler((byte) 87, ":", AFlag.PUNCTUATION),
      new SimpleElementAssembler((byte) 86, "Constant",
          AFlag.NEWLINE_BEFORE_SPACE_AFTER),
      new SimpleElementAssembler((byte) 88, "import"),
      new SimpleElementAssembler((byte) 89, "*"),
      new SimpleElementAssembler((byte) 90, "class", AFlag.FUNCTION_STYLE),
      new SimpleElementAssembler((byte) 91, "end-class",
          AFlag.END_FUNCTION_STYLE),
      new SimpleElementAssembler((byte) 94, "property",
          AFlag.NEWLINE_BEFORE_SPACE_AFTER),
      new SimpleElementAssembler((byte) 95, "get"),
      new SimpleElementAssembler((byte) 96, "readonly"),
      new SimpleElementAssembler((byte) 97, "private", AFlag.ELSE_STYLE),
      new SimpleElementAssembler((byte) 98, "instance"),
      new SimpleElementAssembler((byte) 99, "method",
          AFlag.NEWLINE_BEFORE | AFlag.SPACE_AFTER
          | AFlag.INCREASE_INDENT_ONCE),
      new SimpleElementAssembler((byte) 100, "end-method",
          AFlag.END_FUNCTION_STYLE),
      new SimpleElementAssembler((byte) 101, "try",
          AFlag.SPACE_BEFORE_NEWLINE_AFTER | AFlag.INCREASE_INDENT),
      new SimpleElementAssembler((byte) 102, "catch", AFlag.DECREASE_INDENT),
      new SimpleElementAssembler((byte) 103, "end-try"),
      new SimpleElementAssembler((byte) 104, "throw"),
      new SimpleElementAssembler((byte) 105, "create"),
      new SimpleElementAssembler((byte) 106, "end-get"),
      new SimpleElementAssembler((byte) 107, "end-set"),
      new EmbeddedStringAssembler((byte) 109, "/+ ", " +/",
          AFlag.NEWLINE_BEFORE_AND_AFTER)
    };

    // Initialize hash table of assemblers, indexed by start byte.
    assemblerTable = new HashMap<Byte, ElementAssembler>();
    for (ElementAssembler p : allAssemblers) {
      assemblerTable.put(new Byte(p.getStartByte()), p);
    }
  }

  /**
   * Creates a new Assembler instance for a particular PeopleCode program.
   * @param bstream the bytestream for the PeopleCode program to assemble
   */
  public Assembler(final PeopleCodeByteStream bstream) {
    this.stream = bstream;
    // Program begins at byte 37.
    this.stream.setCursorPos(37);
  }

  /**
   * Begins assembly of the underlying PeopleCode bytestream.
   */
  public void assemble() {
    byte b;
    do {
      b = this.assembleNextByte();
    } while (b != (byte) 7);
  }

  /**
   * Retrieves and assembles the next byte in the stream into the
   * accumulated text of the PeopleCode program.
   * @return the byte that was just assembled and added to the text
   *   form of the PeopleCode program being constructed
   */
  private byte assembleNextByte() {
    final byte b = this.stream.readNextByte();
    if (b == (byte) 7) {
      // 0x07 signals end of program.
      return b;
    }

    //log.debug(String.formatBitmask("Assembling byte: 0x%02X", b));

    final ElementAssembler a = assemblerTable.get(new Byte(b));
    if (a == null) {
      throw new OPSAssembleException(String.format(
          "Encountered unknown byte: 0x%02X on %s",
          b, this.stream.prog.getDescriptor()));
    } else {
      this.isInDeclare = (this.isInDeclare
          && !((this.lastAssembler != null && (this.lastAssembler.formatBitmask
              & AFlag.NEWLINE_AFTER) > 0)
                    || (this.lastAssembler.formatBitmask == AFlag.SEMICOLON)));

      if (this.lastAssembler != null
        && !this.isInDeclare
        && (((this.lastAssembler.formatBitmask
            & AFlag.INCREASE_INDENT) > 0)
          || ((this.lastAssembler.formatBitmask
            & AFlag.INCREASE_INDENT_ONCE) > 0
            && this.nIndent == 0))) {
        this.nIndent++;
      }

      if ((a.formatBitmask & AFlag.RESET_INDENT_BEFORE) > 0
          && !this.isInDeclare) {
        this.nIndent = 0;
      }

      if ((a.formatBitmask & AFlag.DECREASE_INDENT) > 0
          && this.nIndent > 0 && !this.isInDeclare) {
        this.nIndent--;
      }

      if (!this.firstLine
        && a.formatBitmask != AFlag.PUNCTUATION
        && (a.formatBitmask & AFlag.SEMICOLON) == 0
        && !this.isInDeclare
        && (((this.lastAssembler != null
                && ((this.lastAssembler.formatBitmask
                        & AFlag.NEWLINE_AFTER) > 0)
                  || (this.lastAssembler.formatBitmask == AFlag.SEMICOLON))
              &&  (a.formatBitmask & AFlag.COMMENT_ON_SAME_LINE) == 0)
          || ((a.formatBitmask & AFlag.NEWLINE_BEFORE) > 0))
          || ((a.formatBitmask & AFlag.NEWLINE_ONCE) > 0 && !this.didNewline
                && this.stream.readAhead() != (byte) 21)) {

        this.stream.appendAssembledText('\n');
        this.startOfLine = true;
        this.didNewline = true;
      } else {
        if (!this.startOfLine
          && !this.wroteSpace
          && (a.formatBitmask != AFlag.PUNCTUATION)
          && (a.formatBitmask != AFlag.SEMICOLON)
            && (((this.lastAssembler != null
                      && (this.lastAssembler.formatBitmask
                            & AFlag.SPACE_AFTER) > 0))
              ||  (a.formatBitmask & AFlag.SPACE_BEFORE) > 0)
          && (this.lastAssembler == null
              || ((this.lastAssembler.formatBitmask != AFlag.PUNCTUATION
                    && (this.lastAssembler.formatBitmask
                            & AFlag.NO_SPACE_AFTER) == 0)
                         || ((a.formatBitmask & AFlag.SPACE_BEFORE2) > 0)))
          && (a.formatBitmask & AFlag.NO_SPACE_BEFORE) == 0
          && !((a.formatBitmask & AFlag.L_BRACKET) > 0
            && (this.lastAssembler.formatBitmask & AFlag.R_BRACKET) > 0)) {

          this.stream.appendAssembledText(' ');
          this.wroteSpace = true;
        }
      }

      if (this.startOfLine && a.writesNonBlank()) {
        for (int i = 0; i < this.nIndent + (this.andIndicator ? 2 : 0); i++) {
          this.stream.appendAssembledText("   ");
        }
      }
    }

    this.firstLine = false;
    final int initialByteCursorPos = this.stream.getCursorPos();
    a.assemble(this.stream);
    this.wroteSpace = this.wroteSpace && !a.writesNonBlank();
    this.isInDeclare = this.isInDeclare
        || (a.formatBitmask & AFlag.IN_DECLARE) > 0;
    this.startOfLine = this.startOfLine && !a.writesNonBlank();
    this.didNewline = this.didNewline && (
        this.stream.getCursorPos() == initialByteCursorPos);
    this.andIndicator = (a.formatBitmask & AFlag.AND_INDICATOR) > 0
        || (this.andIndicator && (a.formatBitmask
              & AFlag.COMMENT_ON_SAME_LINE) != 0);
    this.lastAssembler = a;
    if ((a.formatBitmask & AFlag.RESET_INDENT_AFTER) > 0) {
      this.nIndent = 0;
    }

    return b;
  }
}
