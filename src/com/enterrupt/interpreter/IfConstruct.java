package com.enterrupt.interpreter;

import java.util.Stack;
import com.enterrupt.types.*;
import com.enterrupt.parser.TFlag;
import com.enterrupt.parser.Token;
import com.enterrupt.pt_objects.PeopleCodeProg;

public class IfConstruct {

	public static void interpret(PeopleCodeProg prog) throws Exception {

		// Detect: IF, EXPRESSION, THEN, STMT_LIST, END_IF
		if(!prog.readNextToken().flags.contains(TFlag.IF)) {
			System.out.println("[ERROR] Expected IF, did not parse.");
			System.exit(1);
		}

		ExprConstruct.interpret(prog);

		if(!prog.readNextToken().flags.contains(TFlag.THEN)) {
			System.out.println("[ERROR] Expected THEN, did not parse.");
			System.exit(1);
		}

		// Get the value of the expression; expect boolean.
		boolean exprResult = ((BooleanPtr) Interpreter.popFromRuntimeStack()).read();

		if(!exprResult) {
			fastForwardToEndToken(prog);
		} else {
			StmtListConstruct.interpret(prog);
		}

		if(!prog.readNextToken().flags.contains(TFlag.END_IF)) {
			System.out.println("[ERROR] Expected END_IF, did not parse.");
			System.exit(1);
		}

		if(!prog.readNextToken().flags.contains(TFlag.SEMICOLON)) {
			System.out.println("[ERROR] Expected SEMICOLON, did not parse.");
			System.exit(1);
		}
	}

	public static void fastForwardToEndToken(PeopleCodeProg prog) {
		Stack<Token> tokenMarkers = new Stack<Token>();

		while(true) {
			Token t = prog.readNextToken();

			if(t.flags.contains(TFlag.IF)) {
				tokenMarkers.push(t);		// we have reached the starting token for a nested If stmt.
			} else if(t.flags.contains(TFlag.END_IF)) {
				if(tokenMarkers.size() == 0) {
					return;		// we have reached the closing token for the desired If stmt.
				} else {
					tokenMarkers.pop();		// we have reached the closing token for a nested If stmt.
				}
			}
		}
	}
}
