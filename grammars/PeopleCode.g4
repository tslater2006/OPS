grammar PeopleCode;

//******************************************************//
// Parser Rules 									    //
//******************************************************//

program	: classicProg;		// TODO: Add appClassProg as possible option.

classicProg : stmtList ;

stmtList:	stmt* ;

stmt	:	ifStmt ';'?
		|	stmtExpr
	 	;

ifStmt		:	'If' expr 'Then' stmtList 'End-If' ;
stmtExpr	:	expr ;

exprList:	expr (',' expr)* ;

expr	:	'(' expr ')'						# ParenthesizedExpr
		|	ReservedDefnWord '.' OBJECT_ID		# ObjDefnRef
		|	literal								# ExprLiteral
		|	OBJECT_ID ('.' OBJECT_ID)*			# CBufferRef
		|	SYSTEM_VAR							# SystemVar
		|	FUNC_ID '(' exprList? ')' ';'?		# FnCall
		|	expr '=' expr ';'					# AssignStmt
		|	expr '=' expr						# Comparison
		;

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
