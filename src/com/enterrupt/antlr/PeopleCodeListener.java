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
	 * Enter a parse tree produced by {@link PeopleCodeParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(@NotNull PeopleCodeParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(@NotNull PeopleCodeParser.ProgramContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmt(@NotNull PeopleCodeParser.StmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmt(@NotNull PeopleCodeParser.StmtContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#classicProg}.
	 * @param ctx the parse tree
	 */
	void enterClassicProg(@NotNull PeopleCodeParser.ClassicProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#classicProg}.
	 * @param ctx the parse tree
	 */
	void exitClassicProg(@NotNull PeopleCodeParser.ClassicProgContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(@NotNull PeopleCodeParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(@NotNull PeopleCodeParser.ExprContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#fn_call}.
	 * @param ctx the parse tree
	 */
	void enterFn_call(@NotNull PeopleCodeParser.Fn_callContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#fn_call}.
	 * @param ctx the parse tree
	 */
	void exitFn_call(@NotNull PeopleCodeParser.Fn_callContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#defn_ref}.
	 * @param ctx the parse tree
	 */
	void enterDefn_ref(@NotNull PeopleCodeParser.Defn_refContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#defn_ref}.
	 * @param ctx the parse tree
	 */
	void exitDefn_ref(@NotNull PeopleCodeParser.Defn_refContext ctx);
}