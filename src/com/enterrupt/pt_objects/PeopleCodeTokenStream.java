package com.enterrupt.pt_objects;

import com.enterrupt.parser.Token;
import com.enterrupt.parser.TFlag;

public class PeopleCodeTokenStream {

	private PeopleCodeProg prog;
	private int cursorPos;

	public PeopleCodeTokenStream(PeopleCodeProg prog) {
		this.prog = prog;
	}

	public Token readNextToken() {

		/*if(prog instanceof RecordPeopleCodeProg) {
			RecordPeopleCodeProg rprog = (RecordPeopleCodeProg) this.prog;
			if(rprog.RECNAME.equals("DERIVED_ADDR") && rprog.FLDNAME.equals("ADDRESSLONG")) {
				Token t = this.prog.progTokens[this.cursorPos];
				System.out.print(t.flags);
				if(t.flags.contains(TFlag.PURE_STRING)) {
					System.out.print("\t\t\t" + t.pureStrVal);
				}
				System.out.println();
			}
		}*/

		return this.prog.progTokens[this.cursorPos++];
	}

	public Token peekNextToken() {
		return this.prog.progTokens[this.cursorPos];
	}
}
