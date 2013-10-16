package com.enterrupt.tokens;

import com.enterrupt.Parser;
import java.util.Stack;

public class StmtListToken extends Token {

	private Stack<IBlockStartable> blockStack;

	private StmtListToken() {
		super();
		this.flags.add(TFlag.STMT_LIST);
		this.blockStack = new Stack<IBlockStartable>();
	}

	public static Token parse() throws Exception {

		StmtListToken stmtListToken = new StmtListToken();

		boolean cont = true;
		while(cont) {
			//StmtToken stmtToken = StmtToken.parse();

			// TODO: REMOVE, re-enable above line.
			Token t = Parser.parseNextToken();
			System.out.println(t.flags.toString());

			if(t.flags.contains(TFlag.END_OF_BLOCK)) {  // this includes END_OF_PROGRAM

				// IMPORTANT: Return token to the parsing stream for use by the caller,
				// 			  who will need to verify that this END_OF_BLOCK is present.
				Parser.returnToParseStream(t);

				if(stmtListToken.blockStack.size() == 0) {
					return stmtListToken;
				} else {
					IBlockStartable startToken = stmtListToken.blockStack.pop();
					if(!startToken.endsWith(t)) {
						System.out.println("[ERROR] END_OF_BLOCK token does not match block start token.");
						System.exit(1);
					}
				}
			}
		}

		return stmtListToken;
	}
}
