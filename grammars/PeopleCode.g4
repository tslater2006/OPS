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
		|	'Exit'								# StmtExit
		|	'Break'								# StmtBreak
		|	expr '(' exprList? ')'				# StmtFnCall
		|	expr '=' expr						# StmtAssign
		;

varDecl	:	varScopeModifier varTypeModifier VAR_ID ;

varScopeModifier	:	'Global' | 'Component' | 'Local' ;
varTypeModifier		:	'Record' | 'string' ;

ifConstruct	:	'If' expr 'Then' stmtList 'End-If' ;

evaluateConstruct	:	'Evaluate' expr whenClause+ whenOtherClause? 'End-Evaluate' ;
whenClause			:	'When' expr stmtList ;
whenOtherClause		:	'When-Other' stmtList ;

expr	:	'(' expr ')'						# ExprParenthesized
		|	literal								# ExprLiteral
		|	id									# ExprId
		|	expr '.' id							# ExprId
		| 	expr '(' exprList? ')'				# ExprFnCall
		|	expr '=' expr						# ExprComparison
		;

exprList:	expr (',' expr)* ;

defnKeyword	:	'MenuName' | 'Component' | 'Record' ;

literal	:	DecimalLiteral
		|	IntegerLiteral
		|	definitionLiteral
		|	booleanLiteral ;
booleanLiteral	:	'True' | 'true' | 'False' | 'false' ;
definitionLiteral : defnKeyword '.' GENERIC_ID ;

id	:	SYS_VAR_ID | VAR_ID | GENERIC_ID ;

//*******************************************************//
// Lexer Rules 									    	 //
//*******************************************************//

GENERIC_ID		:	[a-zA-Z] [0-9a-zA-Z_]* ;	

DecimalLiteral	:	IntegerLiteral '.' [0-9]+ ;
IntegerLiteral	:	'0' | '1'..'9' '0'..'9'* ;

VAR_ID		:	'&' [a-zA-Z_]+ ;
SYS_VAR_ID	:	'%' [a-zA-Z]+ ;

COMMENT		:	'/*' .*? '*/' -> skip;
WS			:	[ \t\r\n]+ -> skip ;
