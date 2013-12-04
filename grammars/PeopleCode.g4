grammar PeopleCode;

//******************************************************//
// Parser Rules 									    //
//******************************************************//

program	: classicProg;

classicProg : stmtList ;

stmtList:	(stmt ';')* ;

stmt	:	varDecl								# StmtVarDecl
		|	ifConstruct							# StmtIf
		|	evaluateConstruct					# StmtEvaluate
		|	fnCall								# StmtFnCall
		|	expr '=' expr						# StmtAssign
		;

varDecl	:	varScopeModifier varTypeModifier VAR_ID ;

varScopeModifier	:	'Global' | 'Component' | 'Local' ;
varTypeModifier		:	'Record' | 'string' ;

ifConstruct	:	'If' expr 'Then' stmtList 'End-If' ;

//evaluateConstruct	:	'Evaluate' expr ('When' expr stmtList)+ 'End-Evaluate' ;
evaluateConstruct	:	'Evaluate' expr whenClause+ 'End-Evaluate' ;
whenClause			:	'When' expr stmtList ;

expr	:	'(' expr ')'						# ExprParenthesized
		|	defnKeyword '.' OBJECT_ID			# ExprObjDefnRef
		|	literal								# ExprLiteral
		|	OBJECT_ID ('.' OBJECT_ID)*			# ExprCompBufferRef
		|	SYSTEM_VAR							# ExprSystemVar
		|	fnCall								# ExprFnCall
		|	expr '=' expr						# ExprComparison
		;

exprList:	expr (',' expr)* ;
fnCall	:	FUNC_ID '(' exprList? ')' ;

defnKeyword	:	'MenuName' | 'Component' ;

literal	:	DecimalLiteral
		|	IntegerLiteral
		|	BooleanLiteral ;

//*******************************************************//
// Lexer Rules 									    	 //
//*******************************************************//

DecimalLiteral	:	IntegerLiteral '.' [0-9]+ ;
IntegerLiteral	:	'0' | '1'..'9' '0'..'9'* ;
BooleanLiteral	:	'True' | 'true' | 'False' | 'false' ;

VAR_ID		:	'&' [a-zA-Z_]+ ;
OBJECT_ID	:	[A-Z] [A-Z_]* ;
FUNC_ID		:	[a-zA-Z]+ ;
SYSTEM_VAR	:	'%' [a-zA-Z]+ ;
COMMENT		:	'/*' .*? '*/' -> skip;
WS			:	[ \t\r\n]+ -> skip ;
