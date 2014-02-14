package com.enterrupt.bytecode;

/**
 * ============================================================
 * This file contains source code derived from the excellent
 * Decode PeopleCode open source project, maintained by Erik H
 * and available under the ISC license at
 * http://sourceforge.net/projects/decodepcode/. The associated
 * license text has been reproduced here in accordance with
 * the license requirements.
 * ============================================================
 * Copyright (c)2011 Erik H (erikh3@users.sourceforge.net)
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

import java.util.*;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.pt.peoplecode.PeopleCodeByteStream;
import org.apache.logging.log4j.*;

public class Assembler {

	private PeopleCodeByteStream stream;

	private static ElementAssembler[] allAssemblers;
	private static HashMap<Byte, ElementAssembler> assemblerTable;

	private int nIndent = 0;
	private boolean endDetected = false;
	private boolean firstLine = true;
	private boolean in_declare = false;
	private boolean startOfLine = true;
	private boolean and_indicator = false;
	private boolean did_newline = false;
	private boolean wroteSpace = false;
	private ElementAssembler lastAssembler = null;

	private static Logger log = LogManager.getLogger(Assembler.class.getName());

	static {
		// Array of all available assemblers.
		allAssemblers = new ElementAssembler[] {
			new IdentifierAssembler((byte) 0),										// 0x00
			new PureStringAssembler((byte) 1),										// 0x01
			new SimpleElementAssembler((byte) 3, ",", AFlag.NO_SPACE_BEFORE | AFlag.SPACE_AFTER),	// 0x03
			new SimpleElementAssembler((byte) 4, "/"), 	// 0x04
			new SimpleElementAssembler((byte) 5, ".", AFlag.PUNCTUATION),	// 0x05
			new SimpleElementAssembler((byte) 6, "="),								// 0x06
			new SimpleElementAssembler((byte) 8, ">="),	// 0x08
			new SimpleElementAssembler((byte) 9, ">"),	// 0x09
			new PureStringAssembler((byte) 10),									// 0x0A (Function | Method | External Datatype | Class name)
			new SimpleElementAssembler((byte) 11, "(", AFlag.NO_SPACE_AFTER),	// 0x0B
			new SimpleElementAssembler((byte) 12, "<="),	// 0x0C
			new SimpleElementAssembler((byte) 13, "<"),		// 0x0D
			new SimpleElementAssembler((byte) 14, "-"), // 0x0E
			new SimpleElementAssembler((byte) 15, "*"), // 0x0F
			new SimpleElementAssembler((byte) 16, "<>"),	// 0x10
			new NumberAssembler((byte) 17, 14),	// 0x11
			new PureStringAssembler((byte) 18),									// 0x12 (System variable name)
			new SimpleElementAssembler((byte) 19, "+"),	// 0x13
			new SimpleElementAssembler((byte) 20, ")", AFlag.NO_SPACE_BEFORE),	// 0x14
			new SimpleElementAssembler((byte) 21, ";", AFlag.SEMICOLON | AFlag.NEWLINE_AFTER | AFlag.NO_SPACE_BEFORE),	// 0x15
			new EmbeddedStringAssembler((byte) 22, "\"", "\""), 		// 0x16
			new SimpleElementAssembler((byte) 24, "And", AFlag.AND_STYLE),	// 0x18
			new SimpleElementAssembler((byte) 25, "Else", AFlag.ELSE_STYLE), // 0x19
			new SimpleElementAssembler((byte) 26, "End-If", AFlag.ENDIF_STYLE),	// 0x1A
			new SimpleElementAssembler((byte) 27, "Error"),	// 0x1B
			new SimpleElementAssembler((byte) 28, "If", AFlag.IF_STYLE),		// 0x1C
			new SimpleElementAssembler((byte) 29, "Not"),	// 0x1D
			new SimpleElementAssembler((byte) 30, "Or", AFlag.AND_STYLE),
			new SimpleElementAssembler((byte) 31, "Then", AFlag.THEN_STYLE),		// 0x1F
			new SimpleElementAssembler((byte) 32, "Warning"),	// 0x20
			new ReferenceAssembler((byte) 33),										// 0x21
			new SimpleElementAssembler((byte) 35, "|"),	// 0x23
			new CommentAssembler((byte) 36), 										// 0x24
			new SimpleElementAssembler((byte) 37, "While", AFlag.FOR_STYLE), // 0x25
			new SimpleElementAssembler((byte) 38, "End-While", AFlag.ENDIF_STYLE), // 0x26
			new SimpleElementAssembler((byte) 41, "For", AFlag.FOR_STYLE),	// 0x29
			new SimpleElementAssembler((byte) 42, "To"),	// 0x42
			new SimpleElementAssembler((byte) 43, "Step"), // 0x2B
			new SimpleElementAssembler((byte) 44, "End-For", AFlag.ENDIF_STYLE),	// 0x2C
			new SimpleElementAssembler((byte) 45, "", AFlag.NEWLINE_ONCE),	// 0x2D
			new SimpleElementAssembler((byte) 46, "Break", AFlag.SPACE_BEFORE), // 0x2E
			new SimpleElementAssembler((byte) 47, "True", AFlag.SPACE_BEFORE_AND_AFTER2),	// 0x2F
			new SimpleElementAssembler((byte) 48, "False", AFlag.SPACE_BEFORE_AND_AFTER2), // 0x30
			new SimpleElementAssembler((byte) 49, "Declare", AFlag.NEWLINE_BEFORE_SPACE_AFTER | AFlag.IN_DECLARE),  // 0x31
			new SimpleElementAssembler((byte) 50, "Function", AFlag.FUNCTION_STYLE),	// 0x32
			new SimpleElementAssembler((byte) 53, "As"),		// 0x35
			new SimpleElementAssembler((byte) 55, "End-Function", AFlag.END_FUNCTION_STYLE),		// 0x37
			new SimpleElementAssembler((byte) 56, "Return"),		// 0x38
			new SimpleElementAssembler((byte) 57, "Returns"),	// 0x39
			new SimpleElementAssembler((byte) 58, "PeopleCode"),		// 0x3A
			new SimpleElementAssembler((byte) 60, "Evaluate", AFlag.INCREASE_INDENT | AFlag.SPACE_AFTER),
			new SimpleElementAssembler((byte) 61, "When", AFlag.DECREASE_INDENT | AFlag.NEWLINE_BEFORE_SPACE_AFTER | AFlag.INCREASE_INDENT),	// 0x3D
			new SimpleElementAssembler((byte) 62, "When-Other", AFlag.DECREASE_INDENT | AFlag.NEWLINE_BEFORE | AFlag.NEWLINE_AFTER | AFlag.INCREASE_INDENT), // 0x3E
			new SimpleElementAssembler((byte) 63, "End-Evaluate", AFlag.NEWLINE_BEFORE | AFlag.DECREASE_INDENT | AFlag.SPACE_BEFORE), // 0x3F
			new PureStringAssembler((byte) 64),		// 0x40 (PeopleCode Variable Type Name)
			new SimpleElementAssembler((byte) 65, "", AFlag.SPACE_AFTER),	// 0x41
			new SimpleElementAssembler((byte) 66, "", AFlag.PUNCTUATION),	// 0x42
			new SimpleElementAssembler((byte) 67, "Exit"), // 0x43
			new SimpleElementAssembler((byte) 68, "Local", AFlag.NEWLINE_BEFORE_SPACE_AFTER), // 0x44
			new SimpleElementAssembler((byte) 69, "Global", AFlag.NEWLINE_BEFORE_SPACE_AFTER), 		// 0x45
			new SimpleElementAssembler((byte) 71, "@", AFlag.SPACE_BEFORE | AFlag.NO_SPACE_AFTER),	// 0x47
			new ReferenceAssembler((byte) 72), 		// 0x48
			new SimpleElementAssembler((byte) 73, "set"),	// 0x49
			new ReferenceAssembler((byte) 74), 		// 0x4A
			new SimpleElementAssembler((byte) 75, "Null"),		// 0x4B
			new SimpleElementAssembler((byte) 76, "[", AFlag.L_BRACKET | AFlag.SPACE_BEFORE | AFlag.NO_SPACE_AFTER),		// 0x4C
			new SimpleElementAssembler((byte) 77, "]", AFlag.R_BRACKET | AFlag.NO_SPACE_BEFORE | AFlag.SPACE_AFTER),		// 0x4D
			new CommentAssembler((byte) 78, AFlag.NEWLINE_AFTER | AFlag.COMMENT_ON_SAME_LINE | AFlag.SPACE_BEFORE),		// 0x4E
			new SimpleElementAssembler((byte) 79, "", AFlag.NEWLINE_AFTER),
			new NumberAssembler((byte) 80, 18),										// 0x50
			new SimpleElementAssembler((byte) 84, "Component", AFlag.NEWLINE_BEFORE_SPACE_AFTER),			// 0x54
			new CommentAssembler((byte) 85, AFlag.NEWLINE_AFTER | AFlag.COMMENT_ON_SAME_LINE | AFlag.SPACE_BEFORE),		// 0x55
			new SimpleElementAssembler((byte) 87, ":", AFlag.PUNCTUATION),
			new SimpleElementAssembler((byte) 86, "Constant", AFlag.NEWLINE_BEFORE_SPACE_AFTER),		// 0x56
			new SimpleElementAssembler((byte) 88, "import"),	// 0x58
			new SimpleElementAssembler((byte) 89, "*"),		// 0x59
			new SimpleElementAssembler((byte) 90, "class", AFlag.FUNCTION_STYLE),	// 0x5A
			new SimpleElementAssembler((byte) 91, "end-class", AFlag.END_FUNCTION_STYLE),		// 0x5B
			new SimpleElementAssembler((byte) 94, "property", AFlag.NEWLINE_BEFORE_SPACE_AFTER),		// 0x5E
			new SimpleElementAssembler((byte) 95, "get"),		// 0x5F
			new SimpleElementAssembler((byte) 96, "readonly"), 		// 0x60
			new SimpleElementAssembler((byte) 97, "private", AFlag.ELSE_STYLE),	// 0x61
			new SimpleElementAssembler((byte) 98, "instance"), 	// 0x62
			new SimpleElementAssembler((byte) 99, "method", AFlag.NEWLINE_BEFORE | AFlag.SPACE_AFTER | AFlag.INCREASE_INDENT_ONCE),	// 0x63
			new SimpleElementAssembler((byte) 100, "end-method", AFlag.END_FUNCTION_STYLE),	// 0x64
			new SimpleElementAssembler((byte) 101, "try", AFlag.SPACE_BEFORE_NEWLINE_AFTER | AFlag.INCREASE_INDENT),	// 0x65
			new SimpleElementAssembler((byte) 102, "catch", AFlag.DECREASE_INDENT),	// 0x66
			new SimpleElementAssembler((byte) 103, "end-try"), // 0x67
			new SimpleElementAssembler((byte) 104, "throw"),		// 0x68
			new SimpleElementAssembler((byte) 105, "create"),	// 0x69
			new SimpleElementAssembler((byte) 106, "end-get"),	// 0x6A
			new SimpleElementAssembler((byte) 107, "end-set"),	// 0x6B
			new EmbeddedStringAssembler((byte) 109, "/+ ", " +/", AFlag.NEWLINE_BEFORE_AND_AFTER)	// 0x6D
		};

		// Initialize hash table of assemblers, indexed by start byte.
		assemblerTable = new HashMap<Byte, ElementAssembler>();
		for(ElementAssembler p : allAssemblers) {
			assemblerTable.put(new Byte(p.getStartByte()), p);
		}
	}

	public Assembler(PeopleCodeByteStream bstream) {
		this.stream = bstream;
		this.stream.setCursorPos(37);			// Program begins at byte 37.
	}

	public void assemble() {
		byte b;
		do {
			b = assembleNextByte();
		} while(b != (byte) 7);
	}

	private byte assembleNextByte() {

		byte b = this.stream.readNextByte();

		if(b == (byte) 7) {
			return b;	// 0x07 signals end of program.
		}

		//log.debug(String.format("Assembling byte: 0x%02X", b));

		ElementAssembler a = assemblerTable.get(new Byte(b));
		if(a == null) {
			throw new EntAssembleException(String.format("Encountered unknown byte: 0x%02X on %s",
				b, this.stream.prog.getDescriptor()));
		} else {
			in_declare = (in_declare &&
				!((lastAssembler != null && (lastAssembler.format & AFlag.NEWLINE_AFTER) > 0)
									  || (lastAssembler.format == AFlag.SEMICOLON)));

			if(lastAssembler != null
				&& !in_declare
				&& (((lastAssembler.format & AFlag.INCREASE_INDENT) > 0)
					|| ((lastAssembler.format & AFlag.INCREASE_INDENT_ONCE) > 0 && nIndent == 0))) {
				nIndent++;
			}

			if((a.format & AFlag.RESET_INDENT_BEFORE) > 0 && !in_declare) {
				nIndent = 0;
			}

			if((a.format & AFlag.DECREASE_INDENT) > 0 && nIndent > 0 && !in_declare) {
				nIndent--;
			}

			if(!firstLine
				&& a.format != AFlag.PUNCTUATION
				&& (a.format & AFlag.SEMICOLON) == 0
				&& !in_declare
				&& (	(	(lastAssembler != null && ((lastAssembler.format & AFlag.NEWLINE_AFTER) > 0)
								|| (lastAssembler.format == AFlag.SEMICOLON))
				  		&&  (a.format & AFlag.COMMENT_ON_SAME_LINE) == 0)
					|| ((a.format & AFlag.NEWLINE_BEFORE) > 0))
					|| ((a.format & AFlag.NEWLINE_ONCE) > 0 && !did_newline && this.stream.readAhead() != (byte) 21)) {

				this.stream.appendAssembledText('\n');
				startOfLine = true;
				did_newline = true;
			} else {
				if(!startOfLine
					&& !wroteSpace
					&& (a.format != AFlag.PUNCTUATION)
					&& (a.format != AFlag.SEMICOLON)
				    && (	(	(lastAssembler != null && (lastAssembler.format & AFlag.SPACE_AFTER) > 0))
							||	(a.format & AFlag.SPACE_BEFORE) > 0)
					&& (lastAssembler == null || ((lastAssembler.format != AFlag.PUNCTUATION
												&& (lastAssembler.format & AFlag.NO_SPACE_AFTER) == 0)
								   		   || ((a.format & AFlag.SPACE_BEFORE2) > 0)))
					&& (a.format & AFlag.NO_SPACE_BEFORE) == 0
					&& !((a.format & AFlag.L_BRACKET) > 0
						&& (lastAssembler.format & AFlag.R_BRACKET) > 0)) {

					this.stream.appendAssembledText(' ');
					wroteSpace = true;
				}
			}

			if(startOfLine && a.writesNonBlank()) {
				for(int i=0; i < nIndent + (and_indicator? 2 : 0); i++) {
					this.stream.appendAssembledText("   ");
				}
			}
		}

		firstLine = false;
		int initialByteCursorPos = this.stream.getCursorPos();
		a.assemble(this.stream);
		wroteSpace = wroteSpace && !a.writesNonBlank();
		in_declare = in_declare || (a.format & AFlag.IN_DECLARE) > 0;
		startOfLine = startOfLine && !a.writesNonBlank();
		did_newline = did_newline && (this.stream.getCursorPos() == initialByteCursorPos);
		and_indicator = (a.format & AFlag.AND_INDICATOR) > 0
			|| (and_indicator && (a.format & AFlag.COMMENT_ON_SAME_LINE) != 0);
		lastAssembler = a;
		if((a.format & AFlag.RESET_INDENT_AFTER) > 0) {
			nIndent = 0;
		}

		return b;
	}
}
