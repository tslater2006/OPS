package com.enterrupt.tokens;

import com.enterrupt.Parser;
import com.enterrupt.Interpreter;
import java.util.Stack;

public class IfToken extends Token implements IBlockStartable {

	private ExprToken expression;
	private StmtListToken stmtList;

	public IfToken() {
		super(TFlag.IF);
	}

	public static void parse() throws Exception {

		// Detect: IF, EXPRESSION, THEN, STMT_LIST, END_IF
		if(!Parser.parseNextToken().flags.contains(TFlag.IF)) {
			System.out.println("[ERROR] Expected IF, did not parse.");
			System.exit(1);
		}

		ExprToken.parse();

		if(!Parser.parseNextToken().flags.contains(TFlag.THEN)) {
			System.out.println("[ERROR] Expected THEN, did not parse.");
			System.exit(1);
		}

		// Get the value of the expression; expect boolean.
		boolean exprResult = ((BooleanToken) Interpreter.popFromExprStack()).getValue();

		if(!exprResult) {
			fastForwardToEndToken();
		} else {
			StmtListToken.parse();
		}

		if(!Parser.parseNextToken().flags.contains(TFlag.END_IF)) {
			System.out.println("[ERROR] Expected END_IF, did not parse.");
			System.exit(1);
		}

		if(!Parser.parseNextToken().flags.contains(TFlag.SEMICOLON)) {
			System.out.println("[ERROR] Expected SEMICOLON, did not parse.");
			System.exit(1);
		}
	}

	public static void fastForwardToEndToken() {
		Stack<Byte> byteMarkers = new Stack<Byte>();
		byte IF = (byte) 28;
		byte END_IF = (byte) 26;

		while(true) {
			byte b = Parser.fastForwardUntil(IF, END_IF);
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

	public boolean endsWith(Token t) {

		/**
		 * TODO: May want to put an identifier in this token
		 * during parsing that concretely links it with its end token
		 * for extra protection.
		 */
		return t.flags.contains(TFlag.END_IF);
	}
}
