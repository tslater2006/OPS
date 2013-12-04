grammar PeopleCode;

//******************************************************//
// Parser Rules 									    //
//******************************************************//

program	: classicProg;		// TODO: Add appClassProg as possible option.

classicProg : stmtList ;

stmtList:	(stmt ';')* ;

stmt	:	ifStmt
		|	assignStmt
		|	stmtExpr
	 	;

ifStmt		:	'If' expr 'Then' stmtList 'End-If' ;
assignStmt	:	expr '=' expr ;
stmtExpr	:	expr ;

exprList:	expr (',' expr)* ;

expr	:	primary
		|	expr '.' IDENTIFIER
		|	expr '(' exprList? ')'		
		|	expr '=' expr ~';'					// comparison
		;

primary	:	'(' expr ')'
		|	'MenuName'
		|	literal
		|	IDENTIFIER ;

literal	:	IntegerLiteral
		|	booleanLiteral ;

booleanLiteral	:	'True' | 'true' | 'False' | 'false' ;

//*******************************************************//
// Lexer Rules 									    	 //
//*******************************************************//

IntegerLiteral	:	'0' | '1'..'9' '0'..'9'* ;
IDENTIFIER	:	'%'? [a-zA-Z_]+ ;
COMMENT		:	'/*' .*? '*/' -> skip;
WS			:	[ \t\r\n]+ -> skip ;	// skip spaces, tabs, newlines, \r (Windows)
