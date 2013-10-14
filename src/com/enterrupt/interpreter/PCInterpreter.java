package com.enterrupt.interpreter;

import com.enterrupt.pt_objects.PeopleCodeProg;
import java.util.HashMap;

public class PCInterpreter {

	private static boolean hasInitialized = false;
	private static ElementParser[] allParsers;
	private static HashMap<Byte, ElementParser> parserTable;

	public static void interpret(PeopleCodeProg prog) throws Exception {

		init();
		prog.setByteCursorPos(37); 	// Program begins at byte 37.
		boolean endDetected = false;

		while(prog.byteCursorPos < prog.progBytes.length && !endDetected) {
			if(endDetected = (prog.getCurrentByte() == (byte) 7)) {
				break;
			}
			byte b = prog.readNextByte();
			ElementParser p = parserTable.get(new Byte(b));
			if(p == null) {
				System.out.println("[ERROR] Reached unimplementable byte.");
				break;
			}
			p.parse(prog);
			System.out.println(prog.getProgText());
		}
	}

	public static void init() {

		if(hasInitialized) return;

		// Array of all available parsers.
		allParsers = new ElementParser[] {
			new CommentParser((byte) 36, PCToken.NEWLINE_BEFORE_AND_AFTER)
		};

		// Initialize hash table of parsers, indexed by start byte.
		parserTable = new HashMap<Byte, ElementParser>();
		for(ElementParser p : allParsers) {
			parserTable.put(new Byte(p.getStartByte()), p);
		}

		hasInitialized = true;
	}
}
