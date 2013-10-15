package com.enterrupt.interpreter;

import com.enterrupt.pt_objects.PeopleCodeProg;
import java.lang.reflect.*;

public class PureStringParser extends StringParser {

	private byte b;

	PureStringParser(byte _b) {
		b = _b;
		format = PFlag.SPACE_BEFORE;
	}

	PureStringParser(byte _b, int _format) {
		this(_b);
		format = _format;
	}

	public byte getStartByte() {
		return b;
	}

	public void parse() throws Exception {
		PCParser.prog.appendProgText(getString());
	}

	public Token interpret() throws Exception {

		Token t = null;
		String str = getString();

		// If string is %______, check for system variable.
		if(str.trim().charAt(0) == '%') {
			try {
				Field f = RunTimeEnvironment.class.getField(str.replaceFirst("%", "SYSVAR_"));
				t = new Token(Token.SYSTEM_VAR);
				return t;
			} catch(NoSuchFieldException nsfe) {
				System.out.println("[FATAL] Unimplemented system variable: " + str);
				System.exit(1);
			}
		}

		// Check to see if the string represents a system function.
		try {
			Method m = RunTimeEnvironment.class.getMethod(str);
			t = new Token(Token.SYSTEM_FN);
			return t;
		} catch(NoSuchMethodException nsme) {
			/**
			 * TODO: Replace this exit with code that checks for declared/imported functions.
			 */
			System.out.println("[FATAL] Unimplemented method: " + str);
			System.exit(1);
		}
		return t;
	}
}
