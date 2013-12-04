// Generated from /home/mquinn/evm/grammars/PeopleCode.g4 by ANTLR 4.1
package com.enterrupt.antlr4.frontend;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PeopleCodeParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PeopleCodeVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtAssign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtAssign(@NotNull PeopleCodeParser.StmtAssignContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprParenthesized}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprParenthesized(@NotNull PeopleCodeParser.ExprParenthesizedContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtFnCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtFnCall(@NotNull PeopleCodeParser.StmtFnCallContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#fnCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFnCall(@NotNull PeopleCodeParser.FnCallContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprFnCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprFnCall(@NotNull PeopleCodeParser.ExprFnCallContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#classicProg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassicProg(@NotNull PeopleCodeParser.ClassicProgContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprSystemVar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprSystemVar(@NotNull PeopleCodeParser.ExprSystemVarContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#stmtList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtList(@NotNull PeopleCodeParser.StmtListContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ifConstruct}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfConstruct(@NotNull PeopleCodeParser.IfConstructContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(@NotNull PeopleCodeParser.ProgramContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprComparison}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprComparison(@NotNull PeopleCodeParser.ExprComparisonContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtIf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtIf(@NotNull PeopleCodeParser.StmtIfContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprCompBufferRef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprCompBufferRef(@NotNull PeopleCodeParser.ExprCompBufferRefContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprObjDefnRef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprObjDefnRef(@NotNull PeopleCodeParser.ExprObjDefnRefContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(@NotNull PeopleCodeParser.LiteralContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#exprList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprList(@NotNull PeopleCodeParser.ExprListContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprLiteral(@NotNull PeopleCodeParser.ExprLiteralContext ctx);
}