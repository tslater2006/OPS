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
	 * Visit a parse tree produced by {@link PeopleCodeParser#definitionLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefinitionLiteral(@NotNull PeopleCodeParser.DefinitionLiteralContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#funcImport}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncImport(@NotNull PeopleCodeParser.FuncImportContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#relop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelop(@NotNull PeopleCodeParser.RelopContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#varDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDecl(@NotNull PeopleCodeParser.VarDeclContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#classicProg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassicProg(@NotNull PeopleCodeParser.ClassicProgContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprBoolean}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprBoolean(@NotNull PeopleCodeParser.ExprBooleanContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId(@NotNull PeopleCodeParser.IdContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#forConstruct}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForConstruct(@NotNull PeopleCodeParser.ForConstructContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#varTypeModifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarTypeModifier(@NotNull PeopleCodeParser.VarTypeModifierContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ifConstruct}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfConstruct(@NotNull PeopleCodeParser.IfConstructContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#event}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvent(@NotNull PeopleCodeParser.EventContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprConcatenate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprConcatenate(@NotNull PeopleCodeParser.ExprConcatenateContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtExit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtExit(@NotNull PeopleCodeParser.StmtExitContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprLiteral(@NotNull PeopleCodeParser.ExprLiteralContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#exprList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprList(@NotNull PeopleCodeParser.ExprListContext ctx);

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
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtFor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtFor(@NotNull PeopleCodeParser.StmtForContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#evaluateConstruct}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvaluateConstruct(@NotNull PeopleCodeParser.EvaluateConstructContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprFnCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprFnCall(@NotNull PeopleCodeParser.ExprFnCallContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#booleanLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanLiteral(@NotNull PeopleCodeParser.BooleanLiteralContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprStaticReference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprStaticReference(@NotNull PeopleCodeParser.ExprStaticReferenceContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtBreak}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtBreak(@NotNull PeopleCodeParser.StmtBreakContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtEvaluate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtEvaluate(@NotNull PeopleCodeParser.StmtEvaluateContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtVarDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtVarDecl(@NotNull PeopleCodeParser.StmtVarDeclContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#stmtList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtList(@NotNull PeopleCodeParser.StmtListContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtFuncImport}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtFuncImport(@NotNull PeopleCodeParser.StmtFuncImportContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#defnKeyword}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefnKeyword(@NotNull PeopleCodeParser.DefnKeywordContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(@NotNull PeopleCodeParser.ProgramContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#varScopeModifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarScopeModifier(@NotNull PeopleCodeParser.VarScopeModifierContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtIf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtIf(@NotNull PeopleCodeParser.StmtIfContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprId(@NotNull PeopleCodeParser.ExprIdContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprDynamicReference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprDynamicReference(@NotNull PeopleCodeParser.ExprDynamicReferenceContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprEquality}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprEquality(@NotNull PeopleCodeParser.ExprEqualityContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#defnPath}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefnPath(@NotNull PeopleCodeParser.DefnPathContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(@NotNull PeopleCodeParser.LiteralContext ctx);
}