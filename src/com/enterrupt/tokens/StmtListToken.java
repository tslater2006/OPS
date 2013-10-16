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

	public static void parse() throws Exception {

		StmtListToken stmtListToken = new StmtListToken();

		boolean cont = true;
		while(cont) {

			StmtToken.parse();

			// Detect: END_OF_BLOCK
			Token t = Parser.lookAheadNextToken();

			if(t.flags.contains(TFlag.END_OF_BLOCK)) {  // this includes END_OF_PROGRAM
				if(stmtListToken.blockStack.size() == 0) {
					return;
				} else {
					IBlockStartable startToken = stmtListToken.blockStack.pop();
					if(!startToken.endsWith(t)) {
						System.out.println("[ERROR] END_OF_BLOCK token does not match block start token.");
						System.exit(1);
					}
				}
			}
		}
	}
}
