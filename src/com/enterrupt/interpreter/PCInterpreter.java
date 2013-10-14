package com.enterrupt.interpreter;

import com.enterrupt.pt_objects.PeopleCodeProg;

public class PCInterpreter {

	public static void interpret(PeopleCodeProg prog) {

		int i = 0;
		for(byte b : prog.progtxtbytes) {
			System.out.printf("%d: 0x%02X\n", i, b);
			i++;
		}
	}
}
