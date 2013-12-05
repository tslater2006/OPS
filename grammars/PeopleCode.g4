grammar PeopleCode;

//******************************************************//
// Parser Rules 									    //
//******************************************************//

program	:	stmtList ;

stmtList:	(stmt ';')* stmt? ;	 // The last/only statement doesn't need a semicolon.

// Semicolons are optional after some PeopleCode stmt constructs.
stmt	:	appClassImport						# StmtAppClassImport
		|	funcImport							# StmtFuncImport
		|	varDecl								# StmtVarDecl
		|	ifConstruct							# StmtIf
		|	forConstruct						# StmtFor
		|	evaluateConstruct					# StmtEvaluate
		|	'Exit'								# StmtExit
		|	'Break'								# StmtBreak
		|	expr '=' expr						# StmtAssign
		|	expr '(' exprList? ')'				# StmtFnCall
		;

expr	:	'(' expr ')'								# ExprParenthesized
		|	'@' '(' expr ')'							# ExprDynamicReference
		|	'create' appClassPath '(' exprList? ')'		# ExprCreate
		|	literal										# ExprLiteral
		|	id											# ExprId
		|	expr '.' id									# ExprStaticReference
		| 	expr '(' exprList? ')'						# ExprFnCall
		|	expr ('='|'<>') expr						# ExprEquality
		|	expr (
					'And'<assoc=right>
				|	'Or'<assoc=right>
			) expr										# ExprBoolean
		|	expr '|' expr								# ExprConcatenate
		;

exprList:	expr (',' expr)* ;

varDecl	:	varScopeModifier varTypeModifier VAR_ID (',' VAR_ID)* ;

varScopeModifier	:	'Global' | 'Component' | 'Local' ;
varTypeModifier		:	'Record' | 'string' | 'boolean' | 'Field' | 'integer' | 'number'
					|	'Rowset' 
					|	appClassPath
					;

appClassImport	:	'import' appClassPath ;
appClassPath	:	GENERIC_ID (':' GENERIC_ID)* ;

funcImport	:	'Declare' 'Function' GENERIC_ID 'PeopleCode' recDefnPath event ;
recDefnPath	:	GENERIC_ID ('.' GENERIC_ID)* ;
event		:	'FieldFormula' ;

ifConstruct	:	'If' expr 'Then' stmtList ('Else' stmtList)? 'End-If' ;

forConstruct:	'For' VAR_ID '=' expr 'To' expr stmtList 'End-For' ;

evaluateConstruct	:	'Evaluate' expr ('When' '='? expr stmtList)+
						('When-Other' stmtList)? 'End-Evaluate' ;

defnType	:	'Component' | 'Panel' | 'RecName' | 'Scroll' | 'HTML'
			|	'MenuName' | 'BarName' | 'ItemName' | 'CompIntfc' | 'Image'
			|	'Interlink' | 'StyleSheet' | 'FileLayout' | 'Page' | 'PanelGroup'
			|	'Message' | 'BusProcess' | 'BusEvent' | 'BusActivity' | 'Field'
			|	'Record' | 'Operation' | 'SQL' ;

literal	:	DecimalLiteral
		|	IntegerLiteral
		|	defnLiteral
		|	StringLiteral
		|	boolLiteral
		;
boolLiteral	:	'True' | 'true' | 'False' | 'false' ;
defnLiteral : defnType '.' GENERIC_ID ;

id	:	SYS_VAR_ID | VAR_ID | GENERIC_ID ;

//*******************************************************//
// Lexer Rules 									    	 //
//*******************************************************//

VAR_ID		:	'&' [a-zA-Z_]+ ;
SYS_VAR_ID	:	'%' [a-zA-Z_]+ ;
GENERIC_ID	:	[a-zA-Z] [0-9a-zA-Z_]* ;	

DecimalLiteral	:	IntegerLiteral '.' [0-9]+ ;
IntegerLiteral	:	'0' | '1'..'9' '0'..'9'* ;
StringLiteral	:	'"' ( ~'"' )* '"' ;

REM			:	'rem' .*? ';' -> skip;
COMMENT		:	'/*' .*? '*/' -> skip;
WS			:	[ \t\r\n]+ -> skip ;
