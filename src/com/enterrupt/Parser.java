package com.enterrupt;

import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.pt_objects.PeopleCodeProg;
import java.util.HashMap;
import com.enterrupt.parsers.*;
import com.enterrupt.tokens.*;
import java.util.EnumSet;

public class Parser {

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
	public static Token queuedParseStreamToken;

	public static void init() {

		// Array of all available parsers.
		allParsers = new ElementParser[] {
			new IdentifierParser((byte) 0),										// 0x00
			new SimpleElementParser((byte) 6, TFlag.EQUAL, "="),								// 0x06
			new PureStringParser((byte) 10),									// 0x0A (Function | Method | External Datatype | Class name)
			new SimpleElementParser((byte) 11, TFlag.L_PAREN, "(", PFlags.NO_SPACE_AFTER),	// 0x0B
			new PureStringParser((byte) 18),									// 0x12 (System variable name)
			new SimpleElementParser((byte) 20, TFlag.R_PAREN, ")", PFlags.NO_SPACE_BEFORE),	// 0x14
			new SimpleElementParser((byte) 21, TFlag.SEMICOLON, ";", PFlags.SEMICOLON | PFlags.NEWLINE_AFTER | PFlags.NO_SPACE_BEFORE),	// 0x15
			new SimpleElementParser((byte) 26, TFlag.END_IF, "End-If", PFlags.ENDIF_STYLE),	// 0x1A
			new SimpleElementParser((byte) 28, TFlag.IF, "If", PFlags.IF_STYLE),			// 0x1C
			new SimpleElementParser((byte) 31, TFlag.THEN, "Then", PFlags.THEN_STYLE),		// 0x1F
			new ReferenceParser((byte) 33),										// 0x21
			new CommentParser((byte) 36), 										// 0x24
			new SimpleElementParser((byte) 47, TFlag.TRUE, "True", PFlags.SPACE_BEFORE_AND_AFTER2),	// 0x2F
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
		queuedParseStreamToken = null;
	}

	public static void returnToParseStream(Token t) {
		if(queuedParseStreamToken != null) {
			System.out.println("[ERROR] There is already a token queued in the parse stream; parser only " +
				"supports 1 character of look-ahead.");
			System.exit(1);
		}
		queuedParseStreamToken = t;
	}

	public static void interpret(PeopleCodeProg p) throws Exception {

		System.out.println("Interpreting PC...");
		reset();
		prog = p;
		prog.interpretFlag = true;
		Interpreter.init();
		prog.setByteCursorPos(37);			// Program begins at byte 37.

		StmtListToken.parse();

		// Detect: END_OF_PROGRAM
		if(!parseNextToken().flags.contains(TFlag.END_OF_PROGRAM)) {
			System.out.println("[ERROR] Expected END_OF_PROGRAM");
			System.exit(1);
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

		while(prog.byteCursorPos < prog.progBytes.length) {
			Token t = parseNextToken();
			if(p.getCurrentByte() == (byte) 7) {		// Signals end of program.
				break;
			}
		}

		System.out.println(prog.getProgText());
		prog.verifyEntireProgramText();
	}

	public static Token parseNextToken() throws Exception {

		Token t = null;

		// If a token has been returned to the parse stream, return it immediately.
		if(queuedParseStreamToken != null) {
			t = queuedParseStreamToken;
			queuedParseStreamToken = null;
			return t;
		}

		byte b = prog.readNextByte();

		if(b == (byte) 7) {
			return new Token(EnumSet.of(TFlag.END_OF_PROGRAM, TFlag.END_OF_BLOCK));
		}

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
		int initialByteCursorPos = prog.byteCursorPos;
		if(prog.interpretFlag) {
			t = p.interpret();
		} else {
			p.parse();
		}
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
