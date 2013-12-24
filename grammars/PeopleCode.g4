grammar PeopleCode;

@lexer::members {
	public static final int REFERENCES = 1;
	public static final int WHITESPACE = 2;
}

//******************************************************//
// Parser Rules 									    //
//******************************************************//

program	:	stmtList ;

// Multiple semicolons may be present; the last/only statement may not have a semicolon.
stmtList:	(stmt ';'+)* stmt? ;	 

stmt	:	appClassImport						# StmtAppClassImport
		|	extFuncImport						# StmtExternalFuncImport
		|	classDeclaration					# StmtClassDeclaration
		|	methodImpl							# StmtMethodImpl
		|	getImpl								# StmtGetImpl
		|	setImpl								# StmtSetImpl
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
		|	'throw' expr						# StmtThrow
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
		|	expr ('<='|'>='|'<'|'>') expr	# ExprComparison
		|	expr ('='|'<>') expr			# ExprEquality
		|	expr (
					op='And'<assoc=right>
				|	op='Or'<assoc=right>
			) expr							# ExprBoolean // order: Not, And, then Or
		|	expr '|' expr					# ExprConcatenate
		;

exprList:	expr (',' expr)* ;

varDeclaration	:	varScope=GENERIC_ID varType varDeclarator (',' varDeclarator)* ;
varDeclarator	:	VAR_ID ('=' expr)? ;

// - varType catches multi-dimensional arrays (i.e., "array of array of Record")
// - GENERIC_ID catches both primitive (i.e, "any", "integer") and complex (i.e., "Record")
//   types, in addition to app class names without path prefixes (i.e., "Address" for "EO:CA:Address")
varType		:	'array' ('of' varType)? | appClassPath | GENERIC_ID	;

appClassImport	:	'import' (appPkgPath|appClassPath) ;
appPkgPath		:	GENERIC_ID (':' GENERIC_ID)* ':' '*' ;
appClassPath	:	GENERIC_ID (':' GENERIC_ID)+ ;

extFuncImport	:	'Declare' 'Function' GENERIC_ID 'PeopleCode' recDefnPath event ;
recDefnPath	:	GENERIC_ID '.' GENERIC_ID ;
event		:	'FieldFormula' | 'FieldChange' ;

classDeclaration	:	'class' GENERIC_ID classBlock* 'end-class' ;
classBlock			:	'private'? ((method | constant | property | instance) ';')+ ;
method				:	'method' GENERIC_ID formalParamList returnType? ;
constant			:	'Constant' VAR_ID '=' expr ;
property			:	'property' varType GENERIC_ID 'get'? 'set'? 'readonly'? ;
instance			:	'instance' varType VAR_ID (',' VAR_ID)* ;

methodImpl			:	'method' GENERIC_ID stmtList 'end-method' ;
getImpl				:	'get' GENERIC_ID stmtList 'end-get' ;
setImpl				:	'set' GENERIC_ID stmtList 'end-set' ;

funcDeclaration :   'Function' GENERIC_ID formalParamList? returnType? ';'? stmtList 'End-Function' ;
formalParamList	:	'(' ')' | '(' VAR_ID paramType? (',' VAR_ID paramType? )* ')' ;
paramType		:	'As' varType ;
returnType		:	'Returns' varType ;

ifStmt	:	'If' expr 'Then' ';'? stmtList ('Else' ';'? stmtList)? 'End-If' ;

forStmt	:	'For' VAR_ID '=' expr 'To' expr (';' | ('Step' expr))? stmtList 'End-For' ;

whileStmt	:	'While' expr ';'? stmtList 'End-While' ;

evaluateStmt	:	'Evaluate' expr whenBranch+ whenOtherBranch? 'End-Evaluate' ;
whenBranch		:	'When' (op='='|op='>')? expr stmtList ;
whenOtherBranch	:	'When-Other' stmtList ;

tryCatchStmt	:	'try' stmtList 'catch' 'Exception' VAR_ID stmtList 'end-try' ;

createInvocation:	'create' (appClassPath|GENERIC_ID) '(' exprList? ')' ;

literal	:	DecimalLiteral
		|	IntegerLiteral
		|	StringLiteral
		|	BoolLiteral
		;

id	:	SYS_VAR_ID | VAR_ID | GENERIC_ID ;

//*******************************************************//
// Lexer Rules 									    	 //
//*******************************************************//

DecimalLiteral	:	IntegerLiteral '.' [0-9]+ ;
IntegerLiteral	:	'0' | '1'..'9' '0'..'9'* ;
StringLiteral	:	'"' ( ~'"' )* '"' ;
BoolLiteral		: 	'True' | 'False' ;

VAR_ID		:	'&' GENERIC_ID ;
SYS_VAR_ID	:	'%' GENERIC_ID ;
GENERIC_ID	:	[a-zA-Z] [0-9a-zA-Z_#]* ;

REM			:	WS [rR][eE][mM] WS .*? ';' -> skip;
COMMENT_1	:	'/*' .*? '*/' -> skip;
COMMENT_2	:	'<*' .*? '*>' -> skip;
COMMENT_3	:	'/+' .*? '+/' ';'? -> skip;
WS			:	[ \t\r\n]+ -> channel(WHITESPACE);

// Reference indices are emitted on a separate channel.
ENT_REF_IDX	:	'#ENTREF{' IntegerLiteral '}' -> channel(REFERENCES) ;
