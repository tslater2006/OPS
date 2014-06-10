// Generated from /home/opsdev/ops/grammars/PeopleCode.g4 by ANTLR 4.1
package com.enterrupt.antlr4.frontend;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PeopleCodeParser}.
 */
public interface PeopleCodeListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#varDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterVarDeclarator(@NotNull PeopleCodeParser.VarDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#varDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitVarDeclarator(@NotNull PeopleCodeParser.VarDeclaratorContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#ExprAddSub}.
	 * @param ctx the parse tree
	 */
	void enterExprAddSub(@NotNull PeopleCodeParser.ExprAddSubContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#ExprAddSub}.
	 * @param ctx the parse tree
	 */
	void exitExprAddSub(@NotNull PeopleCodeParser.ExprAddSubContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#extFuncImport}.
	 * @param ctx the parse tree
	 */
	void enterExtFuncImport(@NotNull PeopleCodeParser.ExtFuncImportContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#extFuncImport}.
	 * @param ctx the parse tree
	 */
	void exitExtFuncImport(@NotNull PeopleCodeParser.ExtFuncImportContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#ExprBoolean}.
	 * @param ctx the parse tree
	 */
	void enterExprBoolean(@NotNull PeopleCodeParser.ExprBooleanContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#ExprBoolean}.
	 * @param ctx the parse tree
	 */
	void exitExprBoolean(@NotNull PeopleCodeParser.ExprBooleanContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#getImpl}.
	 * @param ctx the parse tree
	 */
	void enterGetImpl(@NotNull PeopleCodeParser.GetImplContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#getImpl}.
	 * @param ctx the parse tree
	 */
	void exitGetImpl(@NotNull PeopleCodeParser.GetImplContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtTryCatch}.
	 * @param ctx the parse tree
	 */
	void enterStmtTryCatch(@NotNull PeopleCodeParser.StmtTryCatchContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtTryCatch}.
	 * @param ctx the parse tree
	 */
	void exitStmtTryCatch(@NotNull PeopleCodeParser.StmtTryCatchContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#classBlockStmt}.
	 * @param ctx the parse tree
	 */
	void enterClassBlockStmt(@NotNull PeopleCodeParser.ClassBlockStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#classBlockStmt}.
	 * @param ctx the parse tree
	 */
	void exitClassBlockStmt(@NotNull PeopleCodeParser.ClassBlockStmtContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#whenBranch}.
	 * @param ctx the parse tree
	 */
	void enterWhenBranch(@NotNull PeopleCodeParser.WhenBranchContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#whenBranch}.
	 * @param ctx the parse tree
	 */
	void exitWhenBranch(@NotNull PeopleCodeParser.WhenBranchContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#evaluateStmt}.
	 * @param ctx the parse tree
	 */
	void enterEvaluateStmt(@NotNull PeopleCodeParser.EvaluateStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#evaluateStmt}.
	 * @param ctx the parse tree
	 */
	void exitEvaluateStmt(@NotNull PeopleCodeParser.EvaluateStmtContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#event}.
	 * @param ctx the parse tree
	 */
	void enterEvent(@NotNull PeopleCodeParser.EventContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#event}.
	 * @param ctx the parse tree
	 */
	void exitEvent(@NotNull PeopleCodeParser.EventContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#whenOtherBranch}.
	 * @param ctx the parse tree
	 */
	void enterWhenOtherBranch(@NotNull PeopleCodeParser.WhenOtherBranchContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#whenOtherBranch}.
	 * @param ctx the parse tree
	 */
	void exitWhenOtherBranch(@NotNull PeopleCodeParser.WhenOtherBranchContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#ExprArrayIndex}.
	 * @param ctx the parse tree
	 */
	void enterExprArrayIndex(@NotNull PeopleCodeParser.ExprArrayIndexContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#ExprArrayIndex}.
	 * @param ctx the parse tree
	 */
	void exitExprArrayIndex(@NotNull PeopleCodeParser.ExprArrayIndexContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtThrow}.
	 * @param ctx the parse tree
	 */
	void enterStmtThrow(@NotNull PeopleCodeParser.StmtThrowContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtThrow}.
	 * @param ctx the parse tree
	 */
	void exitStmtThrow(@NotNull PeopleCodeParser.StmtThrowContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#ifStmt}.
	 * @param ctx the parse tree
	 */
	void enterIfStmt(@NotNull PeopleCodeParser.IfStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#ifStmt}.
	 * @param ctx the parse tree
	 */
	void exitIfStmt(@NotNull PeopleCodeParser.IfStmtContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#instance}.
	 * @param ctx the parse tree
	 */
	void enterInstance(@NotNull PeopleCodeParser.InstanceContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#instance}.
	 * @param ctx the parse tree
	 */
	void exitInstance(@NotNull PeopleCodeParser.InstanceContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#methodImpl}.
	 * @param ctx the parse tree
	 */
	void enterMethodImpl(@NotNull PeopleCodeParser.MethodImplContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#methodImpl}.
	 * @param ctx the parse tree
	 */
	void exitMethodImpl(@NotNull PeopleCodeParser.MethodImplContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtExit}.
	 * @param ctx the parse tree
	 */
	void enterStmtExit(@NotNull PeopleCodeParser.StmtExitContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtExit}.
	 * @param ctx the parse tree
	 */
	void exitStmtExit(@NotNull PeopleCodeParser.StmtExitContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#ExprLiteral}.
	 * @param ctx the parse tree
	 */
	void enterExprLiteral(@NotNull PeopleCodeParser.ExprLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#ExprLiteral}.
	 * @param ctx the parse tree
	 */
	void exitExprLiteral(@NotNull PeopleCodeParser.ExprLiteralContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#funcDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFuncDeclaration(@NotNull PeopleCodeParser.FuncDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#funcDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFuncDeclaration(@NotNull PeopleCodeParser.FuncDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#ExprParenthesized}.
	 * @param ctx the parse tree
	 */
	void enterExprParenthesized(@NotNull PeopleCodeParser.ExprParenthesizedContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#ExprParenthesized}.
	 * @param ctx the parse tree
	 */
	void exitExprParenthesized(@NotNull PeopleCodeParser.ExprParenthesizedContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtGetImpl}.
	 * @param ctx the parse tree
	 */
	void enterStmtGetImpl(@NotNull PeopleCodeParser.StmtGetImplContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtGetImpl}.
	 * @param ctx the parse tree
	 */
	void exitStmtGetImpl(@NotNull PeopleCodeParser.StmtGetImplContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#ExprNot}.
	 * @param ctx the parse tree
	 */
	void enterExprNot(@NotNull PeopleCodeParser.ExprNotContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#ExprNot}.
	 * @param ctx the parse tree
	 */
	void exitExprNot(@NotNull PeopleCodeParser.ExprNotContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#ExprMulDiv}.
	 * @param ctx the parse tree
	 */
	void enterExprMulDiv(@NotNull PeopleCodeParser.ExprMulDivContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#ExprMulDiv}.
	 * @param ctx the parse tree
	 */
	void exitExprMulDiv(@NotNull PeopleCodeParser.ExprMulDivContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#recDefnPath}.
	 * @param ctx the parse tree
	 */
	void enterRecDefnPath(@NotNull PeopleCodeParser.RecDefnPathContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#recDefnPath}.
	 * @param ctx the parse tree
	 */
	void exitRecDefnPath(@NotNull PeopleCodeParser.RecDefnPathContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#property}.
	 * @param ctx the parse tree
	 */
	void enterProperty(@NotNull PeopleCodeParser.PropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#property}.
	 * @param ctx the parse tree
	 */
	void exitProperty(@NotNull PeopleCodeParser.PropertyContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtWarning}.
	 * @param ctx the parse tree
	 */
	void enterStmtWarning(@NotNull PeopleCodeParser.StmtWarningContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtWarning}.
	 * @param ctx the parse tree
	 */
	void exitStmtWarning(@NotNull PeopleCodeParser.StmtWarningContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtBreak}.
	 * @param ctx the parse tree
	 */
	void enterStmtBreak(@NotNull PeopleCodeParser.StmtBreakContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtBreak}.
	 * @param ctx the parse tree
	 */
	void exitStmtBreak(@NotNull PeopleCodeParser.StmtBreakContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtEvaluate}.
	 * @param ctx the parse tree
	 */
	void enterStmtEvaluate(@NotNull PeopleCodeParser.StmtEvaluateContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtEvaluate}.
	 * @param ctx the parse tree
	 */
	void exitStmtEvaluate(@NotNull PeopleCodeParser.StmtEvaluateContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#stmtList}.
	 * @param ctx the parse tree
	 */
	void enterStmtList(@NotNull PeopleCodeParser.StmtListContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#stmtList}.
	 * @param ctx the parse tree
	 */
	void exitStmtList(@NotNull PeopleCodeParser.StmtListContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#tryCatchStmt}.
	 * @param ctx the parse tree
	 */
	void enterTryCatchStmt(@NotNull PeopleCodeParser.TryCatchStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#tryCatchStmt}.
	 * @param ctx the parse tree
	 */
	void exitTryCatchStmt(@NotNull PeopleCodeParser.TryCatchStmtContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#appPkgPath}.
	 * @param ctx the parse tree
	 */
	void enterAppPkgPath(@NotNull PeopleCodeParser.AppPkgPathContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#appPkgPath}.
	 * @param ctx the parse tree
	 */
	void exitAppPkgPath(@NotNull PeopleCodeParser.AppPkgPathContext ctx);

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
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtIf}.
	 * @param ctx the parse tree
	 */
	void enterStmtIf(@NotNull PeopleCodeParser.StmtIfContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtIf}.
	 * @param ctx the parse tree
	 */
	void exitStmtIf(@NotNull PeopleCodeParser.StmtIfContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#ExprId}.
	 * @param ctx the parse tree
	 */
	void enterExprId(@NotNull PeopleCodeParser.ExprIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#ExprId}.
	 * @param ctx the parse tree
	 */
	void exitExprId(@NotNull PeopleCodeParser.ExprIdContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#formalParamList}.
	 * @param ctx the parse tree
	 */
	void enterFormalParamList(@NotNull PeopleCodeParser.FormalParamListContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#formalParamList}.
	 * @param ctx the parse tree
	 */
	void exitFormalParamList(@NotNull PeopleCodeParser.FormalParamListContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#createInvocation}.
	 * @param ctx the parse tree
	 */
	void enterCreateInvocation(@NotNull PeopleCodeParser.CreateInvocationContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#createInvocation}.
	 * @param ctx the parse tree
	 */
	void exitCreateInvocation(@NotNull PeopleCodeParser.CreateInvocationContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtMethodImpl}.
	 * @param ctx the parse tree
	 */
	void enterStmtMethodImpl(@NotNull PeopleCodeParser.StmtMethodImplContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtMethodImpl}.
	 * @param ctx the parse tree
	 */
	void exitStmtMethodImpl(@NotNull PeopleCodeParser.StmtMethodImplContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#ExprCreate}.
	 * @param ctx the parse tree
	 */
	void enterExprCreate(@NotNull PeopleCodeParser.ExprCreateContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#ExprCreate}.
	 * @param ctx the parse tree
	 */
	void exitExprCreate(@NotNull PeopleCodeParser.ExprCreateContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#ExprConcat}.
	 * @param ctx the parse tree
	 */
	void enterExprConcat(@NotNull PeopleCodeParser.ExprConcatContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#ExprConcat}.
	 * @param ctx the parse tree
	 */
	void exitExprConcat(@NotNull PeopleCodeParser.ExprConcatContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#setImpl}.
	 * @param ctx the parse tree
	 */
	void enterSetImpl(@NotNull PeopleCodeParser.SetImplContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#setImpl}.
	 * @param ctx the parse tree
	 */
	void exitSetImpl(@NotNull PeopleCodeParser.SetImplContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(@NotNull PeopleCodeParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(@NotNull PeopleCodeParser.ParamContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#ExprDotAccess}.
	 * @param ctx the parse tree
	 */
	void enterExprDotAccess(@NotNull PeopleCodeParser.ExprDotAccessContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#ExprDotAccess}.
	 * @param ctx the parse tree
	 */
	void exitExprDotAccess(@NotNull PeopleCodeParser.ExprDotAccessContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtExpr}.
	 * @param ctx the parse tree
	 */
	void enterStmtExpr(@NotNull PeopleCodeParser.StmtExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtExpr}.
	 * @param ctx the parse tree
	 */
	void exitStmtExpr(@NotNull PeopleCodeParser.StmtExprContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtAppClassImport}.
	 * @param ctx the parse tree
	 */
	void enterStmtAppClassImport(@NotNull PeopleCodeParser.StmtAppClassImportContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtAppClassImport}.
	 * @param ctx the parse tree
	 */
	void exitStmtAppClassImport(@NotNull PeopleCodeParser.StmtAppClassImportContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#varType}.
	 * @param ctx the parse tree
	 */
	void enterVarType(@NotNull PeopleCodeParser.VarTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#varType}.
	 * @param ctx the parse tree
	 */
	void exitVarType(@NotNull PeopleCodeParser.VarTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#id}.
	 * @param ctx the parse tree
	 */
	void enterId(@NotNull PeopleCodeParser.IdContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#id}.
	 * @param ctx the parse tree
	 */
	void exitId(@NotNull PeopleCodeParser.IdContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#ExprNegate}.
	 * @param ctx the parse tree
	 */
	void enterExprNegate(@NotNull PeopleCodeParser.ExprNegateContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#ExprNegate}.
	 * @param ctx the parse tree
	 */
	void exitExprNegate(@NotNull PeopleCodeParser.ExprNegateContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#ExprComparison}.
	 * @param ctx the parse tree
	 */
	void enterExprComparison(@NotNull PeopleCodeParser.ExprComparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#ExprComparison}.
	 * @param ctx the parse tree
	 */
	void exitExprComparison(@NotNull PeopleCodeParser.ExprComparisonContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtClassDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterStmtClassDeclaration(@NotNull PeopleCodeParser.StmtClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtClassDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitStmtClassDeclaration(@NotNull PeopleCodeParser.StmtClassDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#exprList}.
	 * @param ctx the parse tree
	 */
	void enterExprList(@NotNull PeopleCodeParser.ExprListContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#exprList}.
	 * @param ctx the parse tree
	 */
	void exitExprList(@NotNull PeopleCodeParser.ExprListContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#varDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVarDeclaration(@NotNull PeopleCodeParser.VarDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#varDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVarDeclaration(@NotNull PeopleCodeParser.VarDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtAssign}.
	 * @param ctx the parse tree
	 */
	void enterStmtAssign(@NotNull PeopleCodeParser.StmtAssignContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtAssign}.
	 * @param ctx the parse tree
	 */
	void exitStmtAssign(@NotNull PeopleCodeParser.StmtAssignContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#appClassPath}.
	 * @param ctx the parse tree
	 */
	void enterAppClassPath(@NotNull PeopleCodeParser.AppClassPathContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#appClassPath}.
	 * @param ctx the parse tree
	 */
	void exitAppClassPath(@NotNull PeopleCodeParser.AppClassPathContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtWhile}.
	 * @param ctx the parse tree
	 */
	void enterStmtWhile(@NotNull PeopleCodeParser.StmtWhileContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtWhile}.
	 * @param ctx the parse tree
	 */
	void exitStmtWhile(@NotNull PeopleCodeParser.StmtWhileContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(@NotNull PeopleCodeParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(@NotNull PeopleCodeParser.ConstantContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtFor}.
	 * @param ctx the parse tree
	 */
	void enterStmtFor(@NotNull PeopleCodeParser.StmtForContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtFor}.
	 * @param ctx the parse tree
	 */
	void exitStmtFor(@NotNull PeopleCodeParser.StmtForContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#forStmt}.
	 * @param ctx the parse tree
	 */
	void enterForStmt(@NotNull PeopleCodeParser.ForStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#forStmt}.
	 * @param ctx the parse tree
	 */
	void exitForStmt(@NotNull PeopleCodeParser.ForStmtContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(@NotNull PeopleCodeParser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(@NotNull PeopleCodeParser.ClassDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtExternalFuncImport}.
	 * @param ctx the parse tree
	 */
	void enterStmtExternalFuncImport(@NotNull PeopleCodeParser.StmtExternalFuncImportContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtExternalFuncImport}.
	 * @param ctx the parse tree
	 */
	void exitStmtExternalFuncImport(@NotNull PeopleCodeParser.StmtExternalFuncImportContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#appClassImport}.
	 * @param ctx the parse tree
	 */
	void enterAppClassImport(@NotNull PeopleCodeParser.AppClassImportContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#appClassImport}.
	 * @param ctx the parse tree
	 */
	void exitAppClassImport(@NotNull PeopleCodeParser.AppClassImportContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtError}.
	 * @param ctx the parse tree
	 */
	void enterStmtError(@NotNull PeopleCodeParser.StmtErrorContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtError}.
	 * @param ctx the parse tree
	 */
	void exitStmtError(@NotNull PeopleCodeParser.StmtErrorContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#returnType}.
	 * @param ctx the parse tree
	 */
	void enterReturnType(@NotNull PeopleCodeParser.ReturnTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#returnType}.
	 * @param ctx the parse tree
	 */
	void exitReturnType(@NotNull PeopleCodeParser.ReturnTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtVarDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterStmtVarDeclaration(@NotNull PeopleCodeParser.StmtVarDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtVarDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitStmtVarDeclaration(@NotNull PeopleCodeParser.StmtVarDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#whileStmt}.
	 * @param ctx the parse tree
	 */
	void enterWhileStmt(@NotNull PeopleCodeParser.WhileStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#whileStmt}.
	 * @param ctx the parse tree
	 */
	void exitWhileStmt(@NotNull PeopleCodeParser.WhileStmtContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#classBlock}.
	 * @param ctx the parse tree
	 */
	void enterClassBlock(@NotNull PeopleCodeParser.ClassBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#classBlock}.
	 * @param ctx the parse tree
	 */
	void exitClassBlock(@NotNull PeopleCodeParser.ClassBlockContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtReturn}.
	 * @param ctx the parse tree
	 */
	void enterStmtReturn(@NotNull PeopleCodeParser.StmtReturnContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtReturn}.
	 * @param ctx the parse tree
	 */
	void exitStmtReturn(@NotNull PeopleCodeParser.StmtReturnContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#ExprFnOrIdxCall}.
	 * @param ctx the parse tree
	 */
	void enterExprFnOrIdxCall(@NotNull PeopleCodeParser.ExprFnOrIdxCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#ExprFnOrIdxCall}.
	 * @param ctx the parse tree
	 */
	void exitExprFnOrIdxCall(@NotNull PeopleCodeParser.ExprFnOrIdxCallContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtSetImpl}.
	 * @param ctx the parse tree
	 */
	void enterStmtSetImpl(@NotNull PeopleCodeParser.StmtSetImplContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtSetImpl}.
	 * @param ctx the parse tree
	 */
	void exitStmtSetImpl(@NotNull PeopleCodeParser.StmtSetImplContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#method}.
	 * @param ctx the parse tree
	 */
	void enterMethod(@NotNull PeopleCodeParser.MethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#method}.
	 * @param ctx the parse tree
	 */
	void exitMethod(@NotNull PeopleCodeParser.MethodContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#ExprDynamicReference}.
	 * @param ctx the parse tree
	 */
	void enterExprDynamicReference(@NotNull PeopleCodeParser.ExprDynamicReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#ExprDynamicReference}.
	 * @param ctx the parse tree
	 */
	void exitExprDynamicReference(@NotNull PeopleCodeParser.ExprDynamicReferenceContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#ExprEquality}.
	 * @param ctx the parse tree
	 */
	void enterExprEquality(@NotNull PeopleCodeParser.ExprEqualityContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#ExprEquality}.
	 * @param ctx the parse tree
	 */
	void exitExprEquality(@NotNull PeopleCodeParser.ExprEqualityContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(@NotNull PeopleCodeParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(@NotNull PeopleCodeParser.LiteralContext ctx);

	/**
	 * Enter a parse tree produced by {@link PeopleCodeParser#StmtFuncDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterStmtFuncDeclaration(@NotNull PeopleCodeParser.StmtFuncDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link PeopleCodeParser#StmtFuncDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitStmtFuncDeclaration(@NotNull PeopleCodeParser.StmtFuncDeclarationContext ctx);
}