package com.enterrupt.interpreter;

import java.util.Stack;
import com.enterrupt.types.*;
import com.enterrupt.parser.TFlag;
import com.enterrupt.parser.Token;
import com.enterrupt.pt_objects.PeopleCodeTokenStream;

public class IfConstruct {

	public static void interpret(PeopleCodeTokenStream stream) {

		// Detect: IF, EXPRESSION, THEN, STMT_LIST, END_IF
		if(!stream.readNextToken().flags.contains(TFlag.IF)) {
			System.out.println("[ERROR] Expected IF, did not parse.");
			System.exit(1);
		}

		ExprConstruct.interpret(stream);

		if(!stream.readNextToken().flags.contains(TFlag.THEN)) {
			System.out.println("[ERROR] Expected THEN, did not parse.");
			System.exit(1);
		}

		// Get the value of the expression; expect boolean.
		boolean exprResult = ((BooleanPtr) Interpreter.popFromRuntimeStack()).read();

		if(!exprResult) {
			fastForwardToEndToken(stream);
		} else {
			StmtListConstruct.interpret(stream);
		}

		if(!stream.readNextToken().flags.contains(TFlag.END_IF)) {
			System.out.println("[ERROR] Expected END_IF, did not parse.");
			System.exit(1);
		}

		if(!stream.readNextToken().flags.contains(TFlag.SEMICOLON)) {
			System.out.println("[ERROR] Expected SEMICOLON, did not parse.");
			System.exit(1);
		}
	}

	public static void fastForwardToEndToken(PeopleCodeTokenStream stream) {
		Stack<Token> tokenMarkers = new Stack<Token>();

		while(true) {
			Token t = stream.readNextToken();

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
