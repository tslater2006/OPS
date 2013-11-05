package com.enterrupt.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.EnumSet;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.pt_objects.PeopleCodeByteStream;

public class Parser {

	private PeopleCodeByteStream stream;

	private static ElementParser[] allParsers;
	private static HashMap<Byte, ElementParser> parserTable;

	private int nIndent = 0;
	private boolean endDetected = false;
	private boolean firstLine = true;
	private boolean in_declare = false;
	private boolean startOfLine = true;
	private boolean and_indicator = false;
	private boolean did_newline = false;
	private boolean wroteSpace = false;
	private ElementParser lastParser = null;

	static {
		// Array of all available parsers.
		allParsers = new ElementParser[] {
			new IdentifierParser((byte) 0),										// 0x00
			new PureStringParser((byte) 1),										// 0x01
			new SimpleElementParser((byte) 3, EnumSet.of(TFlag.COMMA), ",", PFlags.NO_SPACE_BEFORE | PFlags.SPACE_AFTER),	// 0x03
			new SimpleElementParser((byte) 4, EnumSet.of(TFlag.DIVIDE), "/"), 	// 0x04
			new SimpleElementParser((byte) 5, EnumSet.of(TFlag.PERIOD), ".", PFlags.PUNCTUATION),	// 0x05
			new SimpleElementParser((byte) 6, EnumSet.of(TFlag.EQUAL), "="),								// 0x06
			new SimpleElementParser((byte) 8, EnumSet.of(TFlag.GT_OR_EQUAL), ">="),	// 0x08
			new SimpleElementParser((byte) 9, EnumSet.of(TFlag.GREATER_THAN), ">"),	// 0x09
			new PureStringParser((byte) 10),									// 0x0A (Function | Method | External Datatype | Class name)
			new SimpleElementParser((byte) 11, EnumSet.of(TFlag.L_PAREN), "(", PFlags.NO_SPACE_AFTER),	// 0x0B
			new SimpleElementParser((byte) 12, EnumSet.of(TFlag.LT_OR_EQUAL), "<="),	// 0x0C
			new SimpleElementParser((byte) 13, EnumSet.of(TFlag.LESS_THAN), "<"),		// 0x0D
			new SimpleElementParser((byte) 14, EnumSet.of(TFlag.SUBTRACT), "-"), // 0x0E
			new SimpleElementParser((byte) 16, EnumSet.of(TFlag.NOT_EQUAL), "<>"),	// 0x10
			new NumberParser((byte) 17, 14),	// 0x11
			new PureStringParser((byte) 18),									// 0x12 (System variable name)
			new SimpleElementParser((byte) 19, EnumSet.of(TFlag.PLUS), "+"),	// 0x13
			new SimpleElementParser((byte) 20, EnumSet.of(TFlag.R_PAREN), ")", PFlags.NO_SPACE_BEFORE),	// 0x14
			new SimpleElementParser((byte) 21, EnumSet.of(TFlag.SEMICOLON), ";", PFlags.SEMICOLON | PFlags.NEWLINE_AFTER | PFlags.NO_SPACE_BEFORE),	// 0x15
			new EmbeddedStringParser((byte) 22, "\"", "\""), 		// 0x16
			new SimpleElementParser((byte) 24, EnumSet.of(TFlag.AND), "And", PFlags.AND_STYLE),	// 0x18
			new SimpleElementParser((byte) 25, EnumSet.of(TFlag.ELSE), "Else", PFlags.ELSE_STYLE), // 0x19
			new SimpleElementParser((byte) 26, EnumSet.of(TFlag.END_IF, TFlag.END_OF_BLOCK), "End-If", PFlags.ENDIF_STYLE),	// 0x1A
			new SimpleElementParser((byte) 27, EnumSet.of(TFlag.ERROR), "Error"),	// 0x1B
			new SimpleElementParser((byte) 28, EnumSet.of(TFlag.IF), "If", PFlags.IF_STYLE),		// 0x1C
			new SimpleElementParser((byte) 29, EnumSet.of(TFlag.NOT), "Not"),	// 0x1D
			new SimpleElementParser((byte) 30, EnumSet.of(TFlag.OR), "Or", PFlags.AND_STYLE),
			new SimpleElementParser((byte) 31, EnumSet.of(TFlag.THEN), "Then", PFlags.THEN_STYLE),		// 0x1F
			new SimpleElementParser((byte) 32, EnumSet.of(TFlag.WARNING), "Warning"),	// 0x20
			new ReferenceParser((byte) 33),										// 0x21
			new SimpleElementParser((byte) 35, EnumSet.of(TFlag.PIPE), "|"),	// 0x23
			new CommentParser((byte) 36), 										// 0x24
			new SimpleElementParser((byte) 37, EnumSet.of(TFlag.WHILE), "While", PFlags.FOR_STYLE), // 0x25
			new SimpleElementParser((byte) 38, EnumSet.of(TFlag.END_WHILE), "End-While", PFlags.ENDIF_STYLE), // 0x26
			new SimpleElementParser((byte) 41, EnumSet.of(TFlag.FOR), "For", PFlags.FOR_STYLE),	// 0x29
			new SimpleElementParser((byte) 42, EnumSet.of(TFlag.TO), "To"),	// 0x42
			new SimpleElementParser((byte) 43, EnumSet.of(TFlag.STEP), "Step"), // 0x2B
			new SimpleElementParser((byte) 44, EnumSet.of(TFlag.END_FOR), "End-For", PFlags.ENDIF_STYLE),	// 0x2C
			new SimpleElementParser((byte) 45, EnumSet.of(TFlag.DISCARD), "", PFlags.NEWLINE_ONCE),	// 0x2D
			new SimpleElementParser((byte) 46, EnumSet.of(TFlag.BREAK), "Break", PFlags.SPACE_BEFORE), // 0x2E
			new SimpleElementParser((byte) 47, EnumSet.of(TFlag.TRUE), "True", PFlags.SPACE_BEFORE_AND_AFTER2),	// 0x2F
			new SimpleElementParser((byte) 48, EnumSet.of(TFlag.FALSE), "False", PFlags.SPACE_BEFORE_AND_AFTER2), // 0x30
			new SimpleElementParser((byte) 49, EnumSet.of(TFlag.DECLARE), "Declare", PFlags.NEWLINE_BEFORE_SPACE_AFTER | PFlags.IN_DECLARE),  // 0x31
			new SimpleElementParser((byte) 50, EnumSet.of(TFlag.FUNCTION), "Function", PFlags.FUNCTION_STYLE),	// 0x32
			new SimpleElementParser((byte) 53, EnumSet.of(TFlag.AS), "As"),		// 0x35
			new SimpleElementParser((byte) 55, EnumSet.of(TFlag.END_FUNCTION), "End-Function", PFlags.END_FUNCTION_STYLE),		// 0x37
			new SimpleElementParser((byte) 56, EnumSet.of(TFlag.RETURN), "Return"),		// 0x38
			new SimpleElementParser((byte) 57, EnumSet.of(TFlag.RETURNS), "Returns"),	// 0x39
			new SimpleElementParser((byte) 58, EnumSet.of(TFlag.PEOPLECODE), "PeopleCode"),		// 0x3A
			new SimpleElementParser((byte) 60, EnumSet.of(TFlag.EVALUATE), "Evaluate", PFlags.INCREASE_INDENT | PFlags.SPACE_AFTER),
			new SimpleElementParser((byte) 61, EnumSet.of(TFlag.WHEN), "When", PFlags.DECREASE_INDENT | PFlags.NEWLINE_BEFORE_SPACE_AFTER | PFlags.INCREASE_INDENT),	// 0x3D
			new SimpleElementParser((byte) 62, EnumSet.of(TFlag.WHEN_OTHER), "When-Other", PFlags.DECREASE_INDENT | PFlags.NEWLINE_BEFORE | PFlags.NEWLINE_AFTER | PFlags.INCREASE_INDENT), // 0x3E
			new SimpleElementParser((byte) 63, EnumSet.of(TFlag.END_EVALUATE), "End-Evaluate", PFlags.NEWLINE_BEFORE | PFlags.DECREASE_INDENT | PFlags.SPACE_BEFORE), // 0x3F
			new PureStringParser((byte) 64),		// 0x40 (PeopleCode Variable Type Name)
			new SimpleElementParser((byte) 65, EnumSet.of(TFlag.DISCARD), "", PFlags.SPACE_AFTER),	// 0x41
			new SimpleElementParser((byte) 66, EnumSet.of(TFlag.DISCARD), "", PFlags.PUNCTUATION),	// 0x42
			new SimpleElementParser((byte) 67, EnumSet.of(TFlag.EXIT), "Exit"), // 0x43
			new SimpleElementParser((byte) 68, EnumSet.of(TFlag.LOCAL), "Local", PFlags.NEWLINE_BEFORE_SPACE_AFTER), // 0x44
			new SimpleElementParser((byte) 69, EnumSet.of(TFlag.GLOBAL), "Global", PFlags.NEWLINE_BEFORE_SPACE_AFTER), 		// 0x45
			new SimpleElementParser((byte) 71, EnumSet.of(TFlag.AT_SIGN), "@", PFlags.SPACE_BEFORE | PFlags.NO_SPACE_AFTER),	// 0x47
			new SimpleElementParser((byte) 73, EnumSet.of(TFlag.SET), "set"),	// 0x49
			new ReferenceParser((byte) 74), 		// 0x4A
			new SimpleElementParser((byte) 75, EnumSet.of(TFlag.NULL), "Null"),		// 0x4B
			new SimpleElementParser((byte) 76, EnumSet.of(TFlag.L_BRACKET), "[", PFlags.SPACE_BEFORE | PFlags.NO_SPACE_AFTER),		// 0x4C
			new SimpleElementParser((byte) 77, EnumSet.of(TFlag.R_BRACKET), "]", PFlags.NO_SPACE_BEFORE | PFlags.SPACE_AFTER),		// 0x4D
			new CommentParser((byte) 78, PFlags.NEWLINE_AFTER | PFlags.COMMENT_ON_SAME_LINE | PFlags.SPACE_BEFORE),		// 0x4E
			new SimpleElementParser((byte) 79, EnumSet.of(TFlag.DISCARD), "", PFlags.NEWLINE_AFTER),
			new NumberParser((byte) 80, 18),										// 0x50
			new SimpleElementParser((byte) 84, EnumSet.of(TFlag.COMPONENT), "Component", PFlags.NEWLINE_BEFORE_SPACE_AFTER),			// 0x54
			new CommentParser((byte) 85, PFlags.NEWLINE_AFTER | PFlags.COMMENT_ON_SAME_LINE | PFlags.SPACE_BEFORE),		// 0x55
			new SimpleElementParser((byte) 87, EnumSet.of(TFlag.COLON), ":", PFlags.PUNCTUATION),
			new SimpleElementParser((byte) 86, EnumSet.of(TFlag.CONSTANT), "Constant", PFlags.NEWLINE_BEFORE_SPACE_AFTER),		// 0x56
			new SimpleElementParser((byte) 88, EnumSet.of(TFlag.IMPORT), "import"),	// 0x58
			new SimpleElementParser((byte) 89, EnumSet.of(TFlag.WILDCARD_IMPORT), "*"),		// 0x59
			new SimpleElementParser((byte) 90, EnumSet.of(TFlag.CLASS), "class", PFlags.FUNCTION_STYLE),	// 0x5A
			new SimpleElementParser((byte) 91, EnumSet.of(TFlag.END_CLASS), "end-class", PFlags.END_FUNCTION_STYLE),		// 0x5B
			new SimpleElementParser((byte) 94, EnumSet.of(TFlag.PROPERTY), "property", PFlags.NEWLINE_BEFORE_SPACE_AFTER),		// 0x5E
			new SimpleElementParser((byte) 95, EnumSet.of(TFlag.GET), "get"),		// 0x5F
			new SimpleElementParser((byte) 96, EnumSet.of(TFlag.READONLY), "readonly"), 		// 0x60
			new SimpleElementParser((byte) 97, EnumSet.of(TFlag.PRIVATE), "private", PFlags.ELSE_STYLE),	// 0x61
			new SimpleElementParser((byte) 98, EnumSet.of(TFlag.INSTANCE), "instance"), 	// 0x62
			new SimpleElementParser((byte) 99, EnumSet.of(TFlag.METHOD), "method", PFlags.NEWLINE_BEFORE | PFlags.SPACE_AFTER | PFlags.INCREASE_INDENT_ONCE),	// 0x63
			new SimpleElementParser((byte) 100, EnumSet.of(TFlag.END_METHOD), "end-method", PFlags.END_FUNCTION_STYLE),	// 0x64
			new SimpleElementParser((byte) 101, EnumSet.of(TFlag.TRY), "try", PFlags.SPACE_BEFORE_NEWLINE_AFTER | PFlags.INCREASE_INDENT),	// 0x65
			new SimpleElementParser((byte) 102, EnumSet.of(TFlag.CATCH), "catch", PFlags.DECREASE_INDENT),	// 0x66
			new SimpleElementParser((byte) 103, EnumSet.of(TFlag.END_TRY), "end-try"), // 0x67
			new SimpleElementParser((byte) 104, EnumSet.of(TFlag.THROW), "throw"),		// 0x68
			new SimpleElementParser((byte) 105, EnumSet.of(TFlag.CREATE), "create"),	// 0x69
			new SimpleElementParser((byte) 106, EnumSet.of(TFlag.END_GET), "end-get"),	// 0x6A
			new SimpleElementParser((byte) 107, EnumSet.of(TFlag.END_SET), "end-set"),	// 0x6B
			new EmbeddedStringParser((byte) 109, "/+ ", " +/", PFlags.NEWLINE_BEFORE_AND_AFTER)	// 0x6D
		};

		// Initialize hash table of parsers, indexed by start byte.
		parserTable = new HashMap<Byte, ElementParser>();
		for(ElementParser p : allParsers) {
			parserTable.put(new Byte(p.getStartByte()), p);
		}
	}

	public Parser(PeopleCodeByteStream bstream) {

		this.stream = bstream;
		this.stream.setCursorPos(37);			// Program begins at byte 37.
	}

	public Token parseNextToken() throws Exception {

		Token t = null;
		byte b = this.stream.readNextByte();

		if(b == (byte) 7) {
			return new Token(EnumSet.of(TFlag.END_OF_PROGRAM, TFlag.END_OF_BLOCK));
		}

		//System.out.printf("Getting parser for byte: 0x%02X\n", b);

		ElementParser p = parserTable.get(new Byte(b));
		if(p == null) {
			System.out.printf("[ERROR] Reached unimplementable byte: 0x%02X\n", b);
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
					|| ((p.format & PFlags.NEWLINE_ONCE) > 0 && !did_newline && this.stream.readAhead() != (byte) 21)) {

				this.stream.appendParsedText('\n');
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

					this.stream.appendParsedText(' ');
					wroteSpace = true;
				}
			}

			if(startOfLine && p.writesNonBlank()) {
				for(int i=0; i < nIndent + (and_indicator? 2 : 0); i++) {
					this.stream.appendParsedText("   ");
				}
			}
		}

		firstLine = false;
		int initialByteCursorPos = this.stream.getCursorPos();

		t = p.parse(this.stream);

		wroteSpace = wroteSpace && !p.writesNonBlank();
		in_declare = in_declare || (p.format & PFlags.IN_DECLARE) > 0;
		startOfLine = startOfLine && !p.writesNonBlank();
		did_newline = did_newline && (this.stream.getCursorPos() == initialByteCursorPos);
		and_indicator = (p.format & PFlags.AND_INDICATOR) > 0
			|| (and_indicator && (p.format & PFlags.COMMENT_ON_SAME_LINE) != 0);
		lastParser = p;
		if((p.format & PFlags.RESET_INDENT_AFTER) > 0) {
			nIndent = 0;
		}

		return t;
	}
}
