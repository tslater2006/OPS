package com.enterrupt.interpreter;

import java.util.Stack;
import com.enterrupt.types.*;
import com.enterrupt.parser.TFlag;
import com.enterrupt.parser.Token;

public class IfConstruct {

	public static void interpret() throws Exception {

		// Detect: IF, EXPRESSION, THEN, STMT_LIST, END_IF
		if(!Interpreter.parseNextToken().flags.contains(TFlag.IF)) {
			System.out.println("[ERROR] Expected IF, did not parse.");
			System.exit(1);
		}

		ExprConstruct.interpret();

		if(!Interpreter.parseNextToken().flags.contains(TFlag.THEN)) {
			System.out.println("[ERROR] Expected THEN, did not parse.");
			System.exit(1);
		}

		// Get the value of the expression; expect boolean.
		boolean exprResult = ((BooleanPtr) Interpreter.popFromRuntimeStack()).read();

		if(!exprResult) {
			fastForwardToEndToken();
		} else {
			StmtListConstruct.interpret();
		}

		if(!Interpreter.parseNextToken().flags.contains(TFlag.END_IF)) {
			System.out.println("[ERROR] Expected END_IF, did not parse.");
			System.exit(1);
		}

		if(!Interpreter.parseNextToken().flags.contains(TFlag.SEMICOLON)) {
			System.out.println("[ERROR] Expected SEMICOLON, did not parse.");
			System.exit(1);
		}
	}

	public static void fastForwardToEndToken() {
		Stack<Byte> byteMarkers = new Stack<Byte>();
		byte IF = (byte) 28;
		byte END_IF = (byte) 26;

		while(true) {
			byte b = Interpreter.fastForwardUntil(IF, END_IF);
			if(b == IF) {
				byteMarkers.push(b);		// we have reached the starting token for a nested If stmt.
			} else {
				if(byteMarkers.size() == 0) {
					return;		// we have reached the closing token for the desired If stmt.
				} else {
					byteMarkers.pop();		// we have reached the closing token for a nested If stmt.
				}
			}
		}
	}
}
