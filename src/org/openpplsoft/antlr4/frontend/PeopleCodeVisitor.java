// Generated from /home/opsdev/ops/grammars/PeopleCode.g4 by ANTLR 4.1
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
	 * Visit a parse tree produced by {@link PeopleCodeParser#varDeclarator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDeclarator(@NotNull PeopleCodeParser.VarDeclaratorContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprAddSub}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprAddSub(@NotNull PeopleCodeParser.ExprAddSubContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#extFuncImport}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExtFuncImport(@NotNull PeopleCodeParser.ExtFuncImportContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprBoolean}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprBoolean(@NotNull PeopleCodeParser.ExprBooleanContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#getImpl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGetImpl(@NotNull PeopleCodeParser.GetImplContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtTryCatch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtTryCatch(@NotNull PeopleCodeParser.StmtTryCatchContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#classBlockStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassBlockStmt(@NotNull PeopleCodeParser.ClassBlockStmtContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#whenBranch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhenBranch(@NotNull PeopleCodeParser.WhenBranchContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#evaluateStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvaluateStmt(@NotNull PeopleCodeParser.EvaluateStmtContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#event}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvent(@NotNull PeopleCodeParser.EventContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#whenOtherBranch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhenOtherBranch(@NotNull PeopleCodeParser.WhenOtherBranchContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprArrayIndex}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprArrayIndex(@NotNull PeopleCodeParser.ExprArrayIndexContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtThrow}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtThrow(@NotNull PeopleCodeParser.StmtThrowContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ifStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStmt(@NotNull PeopleCodeParser.IfStmtContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#instance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstance(@NotNull PeopleCodeParser.InstanceContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#methodImpl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodImpl(@NotNull PeopleCodeParser.MethodImplContext ctx);

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
	 * Visit a parse tree produced by {@link PeopleCodeParser#funcDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncDeclaration(@NotNull PeopleCodeParser.FuncDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprParenthesized}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprParenthesized(@NotNull PeopleCodeParser.ExprParenthesizedContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtGetImpl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtGetImpl(@NotNull PeopleCodeParser.StmtGetImplContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprNot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprNot(@NotNull PeopleCodeParser.ExprNotContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprMulDiv}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprMulDiv(@NotNull PeopleCodeParser.ExprMulDivContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#recDefnPath}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecDefnPath(@NotNull PeopleCodeParser.RecDefnPathContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#property}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProperty(@NotNull PeopleCodeParser.PropertyContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtWarning}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtWarning(@NotNull PeopleCodeParser.StmtWarningContext ctx);

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
	 * Visit a parse tree produced by {@link PeopleCodeParser#stmtList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtList(@NotNull PeopleCodeParser.StmtListContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#tryCatchStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTryCatchStmt(@NotNull PeopleCodeParser.TryCatchStmtContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#appPkgPath}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAppPkgPath(@NotNull PeopleCodeParser.AppPkgPathContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(@NotNull PeopleCodeParser.ProgramContext ctx);

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
	 * Visit a parse tree produced by {@link PeopleCodeParser#formalParamList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalParamList(@NotNull PeopleCodeParser.FormalParamListContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#createInvocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreateInvocation(@NotNull PeopleCodeParser.CreateInvocationContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtMethodImpl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtMethodImpl(@NotNull PeopleCodeParser.StmtMethodImplContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprCreate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprCreate(@NotNull PeopleCodeParser.ExprCreateContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprConcat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprConcat(@NotNull PeopleCodeParser.ExprConcatContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#setImpl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetImpl(@NotNull PeopleCodeParser.SetImplContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(@NotNull PeopleCodeParser.ParamContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprDotAccess}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprDotAccess(@NotNull PeopleCodeParser.ExprDotAccessContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtExpr(@NotNull PeopleCodeParser.StmtExprContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtAppClassImport}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtAppClassImport(@NotNull PeopleCodeParser.StmtAppClassImportContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#varType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarType(@NotNull PeopleCodeParser.VarTypeContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId(@NotNull PeopleCodeParser.IdContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprNegate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprNegate(@NotNull PeopleCodeParser.ExprNegateContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprComparison}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprComparison(@NotNull PeopleCodeParser.ExprComparisonContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtClassDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtClassDeclaration(@NotNull PeopleCodeParser.StmtClassDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#exprList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprList(@NotNull PeopleCodeParser.ExprListContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#varDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDeclaration(@NotNull PeopleCodeParser.VarDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtAssign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtAssign(@NotNull PeopleCodeParser.StmtAssignContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#appClassPath}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAppClassPath(@NotNull PeopleCodeParser.AppClassPathContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtWhile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtWhile(@NotNull PeopleCodeParser.StmtWhileContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant(@NotNull PeopleCodeParser.ConstantContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtFor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtFor(@NotNull PeopleCodeParser.StmtForContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#forStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStmt(@NotNull PeopleCodeParser.ForStmtContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#classDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDeclaration(@NotNull PeopleCodeParser.ClassDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtExternalFuncImport}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtExternalFuncImport(@NotNull PeopleCodeParser.StmtExternalFuncImportContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#appClassImport}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAppClassImport(@NotNull PeopleCodeParser.AppClassImportContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtError}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtError(@NotNull PeopleCodeParser.StmtErrorContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#returnType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnType(@NotNull PeopleCodeParser.ReturnTypeContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtVarDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtVarDeclaration(@NotNull PeopleCodeParser.StmtVarDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#whileStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStmt(@NotNull PeopleCodeParser.WhileStmtContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#classBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassBlock(@NotNull PeopleCodeParser.ClassBlockContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtReturn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtReturn(@NotNull PeopleCodeParser.StmtReturnContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#ExprFnOrIdxCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprFnOrIdxCall(@NotNull PeopleCodeParser.ExprFnOrIdxCallContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtSetImpl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtSetImpl(@NotNull PeopleCodeParser.StmtSetImplContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#method}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethod(@NotNull PeopleCodeParser.MethodContext ctx);

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
	 * Visit a parse tree produced by {@link PeopleCodeParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(@NotNull PeopleCodeParser.LiteralContext ctx);

	/**
	 * Visit a parse tree produced by {@link PeopleCodeParser#StmtFuncDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtFuncDeclaration(@NotNull PeopleCodeParser.StmtFuncDeclarationContext ctx);
}