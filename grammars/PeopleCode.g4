grammar PeopleCode;

@lexer::members {
	public static final int REFERENCES = 1;
}

//******************************************************//
// Parser Rules 									    //
//******************************************************//

program	:	stmtList ;

// Multiple semicolons may be present; the last/only statement may not have a semicolon.
stmtList:	(stmt ';'+)* stmt? ;	 

// Semicolons are optional after some PeopleCode stmt constructs.
stmt	:	appClassImport						# StmtAppClassImport
		|	extFuncImport						# StmtExternalFuncImport
		|	funcDeclaration						# StmtFuncDeclaration
		|	varDeclaration						# StmtVarDeclaration
		|	ifStmt								# StmtIf
		|	forStmt								# StmtFor
		|	whileStmt							# StmtWhile
		|	evaluateStmt						# StmtEvaluate
		|	tryCatchStmt						# StmtTryCatch
		|	'Exit'								# StmtExit
		|	'Break'								# StmtBreak
		|	'Error' expr						# StmtError
		|	'Warning' expr						# StmtWarning
		|	'Return' expr?						# StmtReturn
		|	expr '=' expr						# StmtAssign	// Must be higher than fn call to properly resolve '=' ambiguity.
		|	expr								# StmtExpr		// For fn calls; the value of expr is not assigned to anything.
		;

expr	:	'(' expr ')'					# ExprParenthesized
		|	'@' expr						# ExprDynamicReference
		|	literal							# ExprLiteral
		|	id								# ExprId
		|	createInvocation				# ExprCreate
		|	expr '.' id						# ExprMethodOrStaticRef
		|	expr '[' exprList ']'			# ExprArrayIndex // it appears that &array[&i, &j] is shorthand for &array[&i, &j]
		| 	expr '(' exprList? ')'			# ExprFnOrRowsetCall
		|	'-' expr						# ExprNegate
		|	'Not' expr						# ExprNot
		|	expr ('*'|'/') expr				# ExprMulDiv
		|	expr ('+'|'-') expr				# ExprAddSub
		|	expr ('<'|'<='|'>'|'>=') expr	# ExprComparison
		|	expr ('='|'<>') expr			# ExprEquality	// splitting symbols out may affect parsing
		|	expr (
					'And'<assoc=right>
				|	'Or'<assoc=right>
			) expr							# ExprBoolean // order: Not, And, then Or
		|	expr '|' expr					# ExprConcatenate
		;

exprList:	expr (',' expr)* ;

varDeclaration	:	varScope varType varDeclarator (',' varDeclarator)* ;
varDeclarator	:	VAR_ID ('=' expr)? ;

varScope	:	'Global' | 'Component' | 'Local' ;
varType		:	'array' ('of' varType)? | 'Record' | 'string' | 'boolean'
			|	'Field' | 'any' | 'datetime'|	'integer' | 'number'
			| 	'Rowset' | 'date' | 'Row' | 'Grid' | 'GridColumn' | 'SQL'
			|	appClassPath | GENERIC_ID	// for app class names w/o paths (i.e., "Address" for "EO:CA:Address")
			;

appClassImport	:	'import' (appPkgPath|appClassPath) ;
appPkgPath		:	GENERIC_ID (':' GENERIC_ID)* ':' '*' ;
appClassPath	:	GENERIC_ID (':' GENERIC_ID)+ ;

extFuncImport	:	'Declare' 'Function' GENERIC_ID 'PeopleCode' recDefnPath event ;
recDefnPath	:	GENERIC_ID '.' GENERIC_ID ;
event		:	'FieldFormula' | 'FieldChange' ;

funcDeclaration :   'Function' GENERIC_ID formalParamList? returnType? ';'? stmtList 'End-Function' ;
formalParamList	:	'(' ')' | '(' VAR_ID paramType? (',' VAR_ID paramType? )* ')' ;
paramType		:	'As' varType ;
returnType		:	'Returns' varType ;

ifStmt	:	'If' expr 'Then' ';'? stmtList ('Else' ';'? stmtList)? 'End-If' ;

forStmt	:	'For' VAR_ID '=' expr 'To' expr (';' | ('Step' expr))? stmtList 'End-For' ;

whileStmt	:	'While' expr ';'? stmtList 'End-While' ;

evaluateStmt	:	'Evaluate' expr ('When' '='? expr stmtList)+
						('When-Other' stmtList)? 'End-Evaluate' ;

tryCatchStmt	:	'try' stmtList 'catch' 'Exception' VAR_ID stmtList 'end-try' ;

createInvocation:	'create' (appClassPath|GENERIC_ID) '(' exprList? ')' ;

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
boolLiteral	:	'True' | 'False' ;
defnLiteral : defnType '.' GENERIC_ID ;

id	:	SYS_VAR_ID | VAR_ID | GENERIC_ID ;

//*******************************************************//
// Lexer Rules 									    	 //
//*******************************************************//

VAR_ID		:	'&' GENERIC_ID ;
SYS_VAR_ID	:	'%' GENERIC_ID ;
GENERIC_ID	:	[a-zA-Z] [0-9a-zA-Z_#]* ;	

DecimalLiteral	:	IntegerLiteral '.' [0-9]+ ;
IntegerLiteral	:	'0' | '1'..'9' '0'..'9'* ;
StringLiteral	:	'"' ( ~'"' )* '"' ;

REM			:	WS [rR][eE][mM] WS .*? ';' -> skip;
COMMENT_1	:	'/*' .*? '*/' -> skip;
COMMENT_2	:	'<*' .*? '*>' -> skip;
WS			:	[ \t\r\n]+ -> skip ;

// Reference indices are emitted on a separate channel.
ENT_REF_IDX	:	'#ENTREF{' IntegerLiteral '}' -> channel(REFERENCES) ;
