package com.enterrupt.interpreter;

import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.pt_objects.PeopleCodeProg;
import java.util.HashMap;

public class PCParser {

	private static ElementParser[] allParsers;
	private static HashMap<Byte, ElementParser> parserTable;

	private static boolean endDetected;
	private static boolean firstLine;
	private static boolean in_declare;
	private static boolean startOfLine;
	private static boolean and_indicator;
	private static boolean did_newline;
	private static boolean wroteSpace;
	private static ElementParser lastParser;
	private static int nIndent;
	public static PeopleCodeProg prog;

	public static void init() {

		// Array of all available parsers.
		allParsers = new ElementParser[] {
			new IdentifierParser((byte) 0),										// 0x00
			new SimpleElementParser((byte) 6, "="),								// 0x06
			new PureStringParser((byte) 10),									// 0x0A (Function | Method | External Datatype | Class name)
			new SimpleElementParser((byte) 11, "(", PCToken.NO_SPACE_AFTER),	// 0x0B
			new PureStringParser((byte) 18),									// 0x12 (System variable name)
			new SimpleElementParser((byte) 20, ")", PCToken.NO_SPACE_BEFORE),	// 0x14
			new SimpleElementParser((byte) 21, ";", PCToken.SEMICOLON | PCToken.NEWLINE_AFTER | PCToken.NO_SPACE_BEFORE),	// 0x15
			new SimpleElementParser((byte) 26, "End-If", PCToken.ENDIF_STYLE),	// 0x1A
			new SimpleElementParser((byte) 28, "If", PCToken.IF_STYLE),			// 0x1C
			new SimpleElementParser((byte) 31, "Then", PCToken.THEN_STYLE),		// 0x1F
			new ReferenceParser((byte) 33),										// 0x21
			new CommentParser((byte) 36), 										// 0x24
			new SimpleElementParser((byte) 47, "True", PCToken.SPACE_BEFORE_AND_AFTER2),	// 0x2F
			new NumberParser((byte) 80, 18)										// 0x50
		};

		// Initialize hash table of parsers, indexed by start byte.
		parserTable = new HashMap<Byte, ElementParser>();
		for(ElementParser p : allParsers) {
			parserTable.put(new Byte(p.getStartByte()), p);
		}
	}

	public static void reset() {
		endDetected = false;
		firstLine = true;
		in_declare = false;
		startOfLine = true;
		and_indicator = false;
		did_newline = false;
		wroteSpace = false;
		ElementParser lastParser = null;
		nIndent = 0;
		prog = null;
	}

	public static void interpret(PeopleCodeProg p) throws Exception {

		System.out.println("Interpreting PC...");
		reset();
		prog = p;
		prog.interpretFlag = true;
		prog.setByteCursorPos(37);			// Program begins at byte 37.

		while(prog.byteCursorPos < prog.progBytes.length && !endDetected) {
			if(endDetected = (prog.getCurrentByte() == (byte) 7)) {
				break;
			}
			Token t = parseNextToken();
		}
	}

	public static void parse(PeopleCodeProg p) throws Exception {

		System.out.println("Parsing PC...");
		reset();
		prog = p;
		prog.interpretFlag = false;
		prog.setByteCursorPos(37);			// Program begins at byte 37.

		/*int debugIdx = 0;
		for(byte b : prog.progBytes) {
			System.out.printf("%d: 0x%02X\n", debugIdx, b);
			debugIdx++;
		}*/

		while(prog.byteCursorPos < prog.progBytes.length && !endDetected) {
			if(endDetected = (prog.getCurrentByte() == (byte) 7)) {
				break;
			}
			parseNextToken();
		}

		System.out.println(prog.getProgText());
		prog.verifyEntireProgramText();
	}

	public static Token parseNextToken() throws Exception {

		byte b = prog.readNextByte();
		Token t = null;

		if(prog.interpretFlag) {
			System.out.printf("Getting parser for byte: 0x%02X\n", b);
		}

		ElementParser p = parserTable.get(new Byte(b));
		if(p == null) {
			System.out.println("[ERROR] Reached unimplementable byte.");
			System.exit(1);
		} else {
			/* TODO: Fill out as needed. */
			in_declare = (in_declare &&
				!((lastParser != null && (lastParser.format & PCToken.NEWLINE_AFTER) > 0)
									  || (lastParser.format == PCToken.SEMICOLON)));

			if(lastParser != null
				&& !in_declare
				&& (((lastParser.format & PCToken.INCREASE_INDENT) > 0)
					|| ((lastParser.format & PCToken.INCREASE_INDENT_ONCE) > 0 && nIndent == 0))) {
				nIndent++;
			}

			if((p.format & PCToken.RESET_INDENT_BEFORE) > 0 && !in_declare) {
				nIndent = 0;
			}

			if((p.format & PCToken.DECREASE_INDENT) > 0 && nIndent > 0 && !in_declare) {
				nIndent--;
			}

			if(!firstLine
				&& p.format != PCToken.PUNCTUATION
				&& (p.format & PCToken.SEMICOLON) == 0
				&& !in_declare
				&& (	(	(lastParser != null && ((lastParser.format & PCToken.NEWLINE_AFTER) > 0)
								|| (lastParser.format == PCToken.SEMICOLON))
				  		&&  (p.format & PCToken.COMMENT_ON_SAME_LINE) == 0)
					|| ((p.format & PCToken.NEWLINE_BEFORE) > 0))
					|| ((p.format & PCToken.NEWLINE_ONCE) > 0 && !did_newline && prog.readAhead() != (byte) 21)) {

				prog.appendProgText('\n');
				startOfLine = true;
				did_newline = true;
			} else {
				if(!startOfLine
					&& !wroteSpace
					&& (p.format != PCToken.PUNCTUATION)
					&& (p.format != PCToken.SEMICOLON)
				    && (	(	(lastParser != null && (lastParser.format & PCToken.SPACE_AFTER) > 0))
							||	(p.format & PCToken.SPACE_BEFORE) > 0)
					&& (lastParser == null || ((lastParser.format != PCToken.PUNCTUATION
												&& (lastParser.format & PCToken.NO_SPACE_AFTER) == 0)
								   		   || ((p.format & PCToken.SPACE_BEFORE2) > 0)))
					&& (p.format & PCToken.NO_SPACE_BEFORE) == 0) {

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
		int initialByteCursorPos = prog.byteCursorPos;
		if(prog.interpretFlag) {
			t = p.interpret();
		} else {
			p.parse();
		}
		wroteSpace = wroteSpace && !p.writesNonBlank();
		in_declare = in_declare || (p.format & PCToken.IN_DECLARE) > 0;
		startOfLine = startOfLine && !p.writesNonBlank();
		did_newline = did_newline && (prog.byteCursorPos == initialByteCursorPos);
		and_indicator = (p.format & PCToken.AND_INDICATOR) > 0
			|| (and_indicator && (p.format & PCToken.COMMENT_ON_SAME_LINE) != 0);
		lastParser = p;
		if((p.format & PCToken.RESET_INDENT_AFTER) > 0) {
			nIndent = 0;
		}

		return t;
	}
}
