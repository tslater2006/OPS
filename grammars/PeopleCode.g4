grammar PeopleCode;

//******************************************************//
// Parser Rules 									    //
//******************************************************//

program	: classicProg;

classicProg : stmtList ;

stmtList:	(stmt ';')* ;

stmt	:	varDecl								# StmtVarDecl
		|	ifConstruct							# StmtIf
		|	forConstruct						# StmtFor
		|	evaluateConstruct					# StmtEvaluate
		|	'Exit'								# StmtExit
		|	'Break'								# StmtBreak
		|	id '(' exprList? ')'				# StmtFnCall
		|	expr '='<assoc=right> expr			# StmtAssign
		;

expr	:	'(' expr ')'						# ExprParenthesized
		|	literal								# ExprLiteral
		|	id									# ExprId
		| 	id '(' exprList? ')'				# ExprFnCall
		|	'@' '(' expr ')'					# ExprDynamicReference
		|	expr ('And') expr					# ExprBoolean
		|	expr op=('<>'|'=') expr				# ExprEquality
		|	expr ('|') expr						# ExprBitwise
		;

exprList:	expr (',' expr)* ;

varDecl	:	varScopeModifier varTypeModifier VAR_ID (',' VAR_ID)* ;

varScopeModifier	:	'Global' | 'Component' | 'Local' ;
varTypeModifier		:	'Record' | 'string' | 'boolean' | 'Field' | 'integer'
					|	'Rowset' ;

ifConstruct	:	'If' expr 'Then' stmtList elseClause? 'End-If' ;
elseClause	:	'Else' stmtList ;

forConstruct:	'For' VAR_ID '=' expr 'To' expr stmtList 'End-For' ;

evaluateConstruct	:	'Evaluate' expr whenClause+ whenOtherClause? 'End-Evaluate' ;
whenClause			:	'When' expr stmtList ;
whenOtherClause		:	'When-Other' stmtList ;

defnKeyword	:	'MenuName' | 'Component' | 'Record' ;

literal	:	DecimalLiteral
		|	IntegerLiteral
		|	definitionLiteral
		|	StringLiteral
		|	booleanLiteral
		;
booleanLiteral	:	'True' | 'true' | 'False' | 'false' ;
definitionLiteral : defnKeyword '.' GENERIC_ID ;

id	:	SYS_VAR_ID | VAR_ID ('.' GENERIC_ID)* | GENERIC_ID ('.' GENERIC_ID)* ;

//*******************************************************//
// Lexer Rules 									    	 //
//*******************************************************//

GENERIC_ID		:	[a-zA-Z] [0-9a-zA-Z_]* ;	

DecimalLiteral	:	IntegerLiteral '.' [0-9]+ ;
IntegerLiteral	:	'0' | '1'..'9' '0'..'9'* ;
StringLiteral	:	'"' ( ~'"' )* '"' ;

VAR_ID		:	'&' [a-zA-Z_]+ ;
SYS_VAR_ID	:	'%' [a-zA-Z_]+ ;

REM			:	'rem' ~[\r\n]* -> skip;
COMMENT		:	'/*' .*? '*/' -> skip;
WS			:	[ \t\r\n]+ -> skip ;
