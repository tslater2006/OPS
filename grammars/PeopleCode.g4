grammar PeopleCode;

//******************************************************//
// Parser Rules 									    //
//******************************************************//

program	: classicProg;		// TODO: Add appClassProg as possible option.

classicProg : stmtList ;

stmtList:	stmt* ;

stmt	:	ifConstruct ';'						# StmtIf
		|	fnCall ';'							# StmtFnCall
		|	expr '=' expr ';'					# StmtAssign
	 	;
ifConstruct	:	'If' expr 'Then' stmtList 'End-If' ;

expr	:	'(' expr ')'						# ExprParenthesized
		|	ReservedDefnWord '.' OBJECT_ID		# ExprObjDefnRef
		|	literal								# ExprLiteral
		|	OBJECT_ID ('.' OBJECT_ID)*			# ExprCompBufferRef
		|	SYSTEM_VAR							# ExprSystemVar
		|	fnCall								# ExprFnCall
		|	expr '=' expr						# ExprComparison
		;

exprList:	expr (',' expr)* ;
fnCall	:	FUNC_ID '(' exprList? ')' ;

literal	:	DecimalLiteral
		|	IntegerLiteral
		|	BooleanLiteral ;

//*******************************************************//
// Lexer Rules 									    	 //
//*******************************************************//

ReservedDefnWord	:	'MenuName' ;
DecimalLiteral	:	IntegerLiteral '.' [0-9]+ ;
IntegerLiteral	:	'0' | '1'..'9' '0'..'9'* ;
BooleanLiteral	:	'True' | 'true' | 'False' | 'false' ;
OBJECT_ID:	[A-Z] [A-Z_]* ;
FUNC_ID:	[a-zA-Z]+ ;
SYSTEM_VAR:		'%' [a-zA-Z]+ ;
COMMENT		:	'/*' .*? '*/' -> skip;
WS			:	[ \t\r\n]+ -> skip ;
