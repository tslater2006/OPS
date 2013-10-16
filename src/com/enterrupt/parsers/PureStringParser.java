package com.enterrupt.parsers;

import com.enterrupt.pt_objects.PeopleCodeProg;
import com.enterrupt.Parser;
import com.enterrupt.tokens.*;
import java.lang.reflect.*;
import com.enterrupt.RunTimeEnvironment;
import java.util.EnumSet;

public class PureStringParser extends StringParser {

	private byte b;

	public PureStringParser(byte _b) {
		b = _b;
		format = PFlags.SPACE_BEFORE;
	}

	public PureStringParser(byte _b, int _format) {
		this(_b);
		format = _format;
	}

	public byte getStartByte() {
		return b;
	}

	public void parse() throws Exception {
		Parser.prog.appendProgText(getString());
	}

	public Token interpret() throws Exception {

		Token t = null;
		String str = getString();

		// If string is %______, check for system variable.
		if(str.trim().charAt(0) == '%') {
			try {
				Field f = RunTimeEnvironment.class.getField(str.replaceFirst("%", "SYSVAR_"));
				t = new Token(EnumSet.of(TFlag.SYSTEM, TFlag.VARIABLE));
				return t;
			} catch(NoSuchFieldException nsfe) {
				System.out.println("[FATAL] Unimplemented system variable: " + str);
				System.exit(1);
			}
		}

		// Check to see if the string represents a system function.
		try {
			Method m = RunTimeEnvironment.class.getMethod(str);
			FunctionToken fnToken = new FunctionToken(TFlag.SYSTEM);
			fnToken.fnTarget = m;
			return fnToken;
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
