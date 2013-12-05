grammar PeopleCode;

//******************************************************//
// Parser Rules 									    //
//******************************************************//

program	: classicProg;

classicProg : stmtList ;

stmtList:	(stmt ';')* ;

stmt	:	funcImport							# StmtFuncImport
		|	varDecl								# StmtVarDecl
		|	ifConstruct							# StmtIf
		|	forConstruct						# StmtFor
		|	evaluateConstruct					# StmtEvaluate
		|	'Exit'								# StmtExit
		|	'Break'								# StmtBreak
		|	expr '='<assoc=right> expr			# StmtAssign
		|	expr '(' exprList? ')'				# StmtFnCall
		;

expr	:	'(' expr ')'						# ExprParenthesized
		|	'@' '(' expr ')'					# ExprDynamicReference
		|	literal								# ExprLiteral
		|	id									# ExprId
		|	expr '.' id							# ExprStaticReference
		| 	expr '(' exprList? ')'				# ExprFnCall
		|	expr ('And') expr					# ExprBoolean
		|	expr relop expr						# ExprEquality
		|	expr ('|') expr						# ExprConcatenate
		;

exprList:	expr (',' expr)* ;

varDecl	:	varScopeModifier varTypeModifier VAR_ID (',' VAR_ID)* ;

varScopeModifier	:	'Global' | 'Component' | 'Local' ;
varTypeModifier		:	'Record' | 'string' | 'boolean' | 'Field' | 'integer' | 'number'
					|	'Rowset' ;

funcImport	:	'Declare' 'Function' GENERIC_ID 'PeopleCode' defnPath event ;
defnPath	:	GENERIC_ID ('.' GENERIC_ID)* ;
event		:	'FieldFormula' ;

ifConstruct	:	'If' expr 'Then' stmtList ('Else' stmtList)? 'End-If' ;

forConstruct:	'For' VAR_ID '=' expr 'To' expr stmtList 'End-For' ;

evaluateConstruct	:	'Evaluate' expr ('When' relop? expr stmtList)+
						('When-Other' stmtList)? 'End-Evaluate' ;

defnKeyword	:	'MenuName' | 'Component' | 'Record' | 'Field' | 'Panel' ;

relop	:	'=' | '<>' ;

literal	:	DecimalLiteral
		|	IntegerLiteral
		|	definitionLiteral
		|	StringLiteral
		|	booleanLiteral
		;
booleanLiteral	:	'True' | 'true' | 'False' | 'false' ;
definitionLiteral : defnKeyword '.' GENERIC_ID ;

id	:	SYS_VAR_ID | VAR_ID | GENERIC_ID ;

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
