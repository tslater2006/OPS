package com.enterrupt.parser;

import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.pt_objects.PeopleCodeProg;
import com.enterrupt.pt_objects.Reference;
import com.enterrupt.pt_objects.RecordPeopleCodeProg;
import com.enterrupt.pt_objects.AppPackagePeopleCodeProg;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.EnumSet;

public class Parser {

	private static ElementParser[] allParsers;
	private static HashMap<Byte, ElementParser> parserTable;

	private int nIndent = 0;
	private PeopleCodeProg prog;
	private boolean endDetected = false;
	private boolean firstLine = true;
	private boolean in_declare = false;
	private boolean startOfLine = true;
	private boolean and_indicator = false;
	private boolean did_newline = false;
	private boolean wroteSpace = false;
	private ElementParser lastParser = null;

	public Parser(PeopleCodeProg prog) {

		this.prog = prog;
		this.prog.resetProgText();
		this.prog.setByteCursorPos(37);			// Program begins at byte 37.

		if(allParsers == null) {

			// Array of all available parsers.
			allParsers = new ElementParser[] {
				new IdentifierParser((byte) 0),										// 0x00
				new PureStringParser((byte) 1),										// 0x01
				new SimpleElementParser((byte) 3, EnumSet.of(TFlag.COMMA), ",", PFlags.NO_SPACE_BEFORE | PFlags.SPACE_AFTER),	// 0x03
				new SimpleElementParser((byte) 5, EnumSet.of(TFlag.PERIOD), ".", PFlags.PUNCTUATION),	// 0x05
				new SimpleElementParser((byte) 6, EnumSet.of(TFlag.EQUAL), "="),								// 0x06
				new PureStringParser((byte) 10),									// 0x0A (Function | Method | External Datatype | Class name)
				new SimpleElementParser((byte) 11, EnumSet.of(TFlag.L_PAREN), "(", PFlags.NO_SPACE_AFTER),	// 0x0B
				new PureStringParser((byte) 18),									// 0x12 (System variable name)
				new SimpleElementParser((byte) 20, EnumSet.of(TFlag.R_PAREN), ")", PFlags.NO_SPACE_BEFORE),	// 0x14
				new SimpleElementParser((byte) 21, EnumSet.of(TFlag.SEMICOLON), ";", PFlags.SEMICOLON | PFlags.NEWLINE_AFTER | PFlags.NO_SPACE_BEFORE),	// 0x15
				new SimpleElementParser((byte) 26, EnumSet.of(TFlag.END_IF, TFlag.END_OF_BLOCK), "End-If", PFlags.ENDIF_STYLE),	// 0x1A
				new SimpleElementParser((byte) 28, EnumSet.of(TFlag.IF), "If", PFlags.IF_STYLE),			// 0x1C
				new SimpleElementParser((byte) 31, EnumSet.of(TFlag.THEN), "Then", PFlags.THEN_STYLE),		// 0x1F
				new ReferenceParser((byte) 33),										// 0x21
				new CommentParser((byte) 36), 										// 0x24
				new SimpleElementParser((byte) 45, EnumSet.of(TFlag.DISCARD), "", PFlags.NEWLINE_ONCE),	// 0x2D
				new SimpleElementParser((byte) 47, EnumSet.of(TFlag.TRUE), "True", PFlags.SPACE_BEFORE_AND_AFTER2),	// 0x2F
				new SimpleElementParser((byte) 49, EnumSet.of(TFlag.DECLARE), "Declare", PFlags.NEWLINE_BEFORE_SPACE_AFTER | PFlags.IN_DECLARE),  // 0x31
				new SimpleElementParser((byte) 50, EnumSet.of(TFlag.FUNCTION), "Function", PFlags.FUNCTION_STYLE),	// 0x32
				new SimpleElementParser((byte) 58, EnumSet.of(TFlag.PEOPLECODE), "PeopleCode"),		// 0x3A
				new SimpleElementParser((byte) 60, EnumSet.of(TFlag.EVALUATE), "Evaluate", PFlags.INCREASE_INDENT | PFlags.SPACE_AFTER),
				new PureStringParser((byte) 64),		// 0x40 (PeopleCode Variable Type Name)
				new SimpleElementParser((byte) 66, EnumSet.of(TFlag.DISCARD), ""),	// 0x42
				new SimpleElementParser((byte) 68, EnumSet.of(TFlag.LOCAL), "Local", PFlags.NEWLINE_BEFORE_SPACE_AFTER), // 0x44
				new SimpleElementParser((byte) 69, EnumSet.of(TFlag.GLOBAL), "Global", PFlags.NEWLINE_BEFORE_SPACE_AFTER), 		// 0x45
				new SimpleElementParser((byte) 79, EnumSet.of(TFlag.DISCARD), "", PFlags.NEWLINE_AFTER),
				new NumberParser((byte) 80, 18),										// 0x50
				new SimpleElementParser((byte) 84, EnumSet.of(TFlag.COMPONENT), "Component", PFlags.NEWLINE_BEFORE_SPACE_AFTER),			// 0x54
				new SimpleElementParser((byte) 87, EnumSet.of(TFlag.COLON), ":", PFlags.PUNCTUATION),
				new SimpleElementParser((byte) 88, EnumSet.of(TFlag.IMPORT), "import"),	// 0x58
				new SimpleElementParser((byte) 101, EnumSet.of(TFlag.TRY), "try", PFlags.SPACE_BEFORE_NEWLINE_AFTER)	// 0x65
			};

			// Initialize hash table of parsers, indexed by start byte.
			parserTable = new HashMap<Byte, ElementParser>();
			for(ElementParser p : allParsers) {
				parserTable.put(new Byte(p.getStartByte()), p);
			}
		}
	}

	public Token parseNextToken() throws Exception {

		Token t = null;
		byte b = prog.readNextByte();

		if(b == (byte) 7) {
			return new Token(EnumSet.of(TFlag.END_OF_PROGRAM, TFlag.END_OF_BLOCK));
		}

		System.out.printf("Getting parser for byte: 0x%02X\n", b);

		ElementParser p = parserTable.get(new Byte(b));
		if(p == null) {
			System.out.println("[ERROR] Reached unimplementable byte.");
			System.exit(1);
		} else {

			/* TODO: Fill out as needed. */
			in_declare = (in_declare &&
				!((lastParser != null && (lastParser.format & PFlags.NEWLINE_AFTER) > 0)
									  || (lastParser.format == PFlags.SEMICOLON)));

			if(lastParser != null
				&& !in_declare
				&& (((lastParser.format & PFlags.INCREASE_INDENT) > 0)
					|| ((lastParser.format & PFlags.INCREASE_INDENT_ONCE) > 0 && nIndent == 0))) {
				nIndent++;
			}

			if((p.format & PFlags.RESET_INDENT_BEFORE) > 0 && !in_declare) {
				nIndent = 0;
			}

			if((p.format & PFlags.DECREASE_INDENT) > 0 && nIndent > 0 && !in_declare) {
				nIndent--;
			}

			if(!firstLine
				&& p.format != PFlags.PUNCTUATION
				&& (p.format & PFlags.SEMICOLON) == 0
				&& !in_declare
				&& (	(	(lastParser != null && ((lastParser.format & PFlags.NEWLINE_AFTER) > 0)
								|| (lastParser.format == PFlags.SEMICOLON))
				  		&&  (p.format & PFlags.COMMENT_ON_SAME_LINE) == 0)
					|| ((p.format & PFlags.NEWLINE_BEFORE) > 0))
					|| ((p.format & PFlags.NEWLINE_ONCE) > 0 && !did_newline && prog.readAhead() != (byte) 21)) {

				prog.appendProgText('\n');
				startOfLine = true;
				did_newline = true;
			} else {
				if(!startOfLine
					&& !wroteSpace
					&& (p.format != PFlags.PUNCTUATION)
					&& (p.format != PFlags.SEMICOLON)
				    && (	(	(lastParser != null && (lastParser.format & PFlags.SPACE_AFTER) > 0))
							||	(p.format & PFlags.SPACE_BEFORE) > 0)
					&& (lastParser == null || ((lastParser.format != PFlags.PUNCTUATION
												&& (lastParser.format & PFlags.NO_SPACE_AFTER) == 0)
								   		   || ((p.format & PFlags.SPACE_BEFORE2) > 0)))
					&& (p.format & PFlags.NO_SPACE_BEFORE) == 0) {

					prog.appendProgText(' ');
					wroteSpace = true;
				}
			}

			if(startOfLine && p.writesNonBlank()) {
				for(int i=0; i < nIndent + (and_indicator? 2 : 0); i++) {
					prog.appendProgText("   ");
				}
			}
		}

		firstLine = false;
		int initialByteCursorPos = this.prog.byteCursorPos;

		t = p.parse(this.prog);

		wroteSpace = wroteSpace && !p.writesNonBlank();
		in_declare = in_declare || (p.format & PFlags.IN_DECLARE) > 0;
		startOfLine = startOfLine && !p.writesNonBlank();
		did_newline = did_newline && (prog.byteCursorPos == initialByteCursorPos);
		and_indicator = (p.format & PFlags.AND_INDICATOR) > 0
			|| (and_indicator && (p.format & PFlags.COMMENT_ON_SAME_LINE) != 0);
		lastParser = p;
		if((p.format & PFlags.RESET_INDENT_AFTER) > 0) {
			nIndent = 0;
		}

		return t;
	}
}
