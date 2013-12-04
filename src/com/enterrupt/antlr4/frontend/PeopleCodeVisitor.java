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
	 * Visit a parse tree produced by {@link PeopleCodeParser#stmtExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtExpr(@NotNull PeopleCodeParser.StmtExprContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmt(@NotNull PeopleCodeParser.StmtContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#classicProg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassicProg(@NotNull PeopleCodeParser.ClassicProgContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#CBufferRef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCBufferRef(@NotNull PeopleCodeParser.CBufferRefContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#Comparison}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison(@NotNull PeopleCodeParser.ComparisonContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#stmtList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtList(@NotNull PeopleCodeParser.StmtListContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ObjDefnRef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjDefnRef(@NotNull PeopleCodeParser.ObjDefnRefContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(@NotNull PeopleCodeParser.ProgramContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#SystemVar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSystemVar(@NotNull PeopleCodeParser.SystemVarContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ParenthesizedExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesizedExpr(@NotNull PeopleCodeParser.ParenthesizedExprContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#AssignStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignStmt(@NotNull PeopleCodeParser.AssignStmtContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ifStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStmt(@NotNull PeopleCodeParser.IfStmtContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(@NotNull PeopleCodeParser.LiteralContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#FnCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFnCall(@NotNull PeopleCodeParser.FnCallContext ctx);

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