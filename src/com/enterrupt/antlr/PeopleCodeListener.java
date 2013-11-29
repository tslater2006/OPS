// Generated from /home/mquinn/evm/grammars/PeopleCode.g4 by ANTLR 4.1
package com.enterrupt.antlr;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PeopleCodeParser}.
 */
public interface PeopleCodeListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#r}.
	 * @param ctx the parse tree
	 */
	void enterR(@NotNull PeopleCodeParser.RContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#r}.
	 * @param ctx the parse tree
	 */
	void exitR(@NotNull PeopleCodeParser.RContext ctx);
}