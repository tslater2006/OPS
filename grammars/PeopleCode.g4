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
		|	fnCall								# StmtFnCall
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
		|	defnRef								# ExprDefnRef
		|	literal								# ExprLiteral
		|	compBufferRef						# ExprCompBufferRef
		|	SYS_VAR_ID							# ExprSystemVar
		|	VAR_ID								# ExprVar
		|	fnCall								# ExprFnCall
		|	expr '=' expr						# ExprComparison
		;

exprList:	expr (',' expr)* ;
fnCall	:	FUNC_ID '(' exprList? ')' ;

defnRef		:	defnKeyword '.' OBJECT_ID ;
defnKeyword	:	'MenuName' | 'Component' | 'Record' ;

compBufferRef		:	OBJECT_ID ('.' OBJECT_ID)* ('.' bufferFldProperty)* ;
bufferFldProperty	:	'Visible' ;	

literal	:	DecimalLiteral
		|	IntegerLiteral
		|	booleanLiteral ;
booleanLiteral	:	'True' | 'true' | 'False' | 'false' ;


//*******************************************************//
// Lexer Rules 									    	 //
//*******************************************************//

DecimalLiteral	:	IntegerLiteral '.' [0-9]+ ;
IntegerLiteral	:	'0' | '1'..'9' '0'..'9'* ;

VAR_ID		:	'&' [a-zA-Z_]+ ;
OBJECT_ID	:	[A-Z] [0-9A-Z_]* ;
FUNC_ID		:	[a-zA-Z]+ ;
SYS_VAR_ID	:	'%' [a-zA-Z]+ ;
COMMENT		:	'/*' .*? '*/' -> skip;
WS			:	[ \t\r\n]+ -> skip ;
