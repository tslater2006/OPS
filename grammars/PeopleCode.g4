grammar PeopleCode;

program	: classicProg;		// TODO: Add appClassProg as possible option.

classicProg : stmt* 
			| CBUFFER_REF '=' SYSTEM_VAR ';'
			;

stmt:	(
			'If' expr 'Then' stmt* 'End-If'
		|	fn_call
		|	CBUFFER_REF '=' expr				// assignment
	 	) ';'
		;

expr	:	fn_call
		|	expr '=' expr						// comparison
		|	CBUFFER_REF
		|	defn_ref							// i.e., MenuName.<?>
		|	SYSTEM_VAR
		|	NUMBER
		|	BOOLEAN
		;

fn_call	:	FN_NAME '(' expr (',' expr)* ')' ;

defn_ref:	'MenuName' '.' OBJECT_REF;		// TODO: Add more reserved defn keywords

BOOLEAN		:	'True' | 'true' | 'False' | 'false' ;
NUMBER		:	[0-9]+ ;			// TODO: may need to exclude leading 0s.
SYSTEM_VAR	:	'%' [a-zA-Z]+ ;
CBUFFER_REF :	OBJECT_REF '.' OBJECT_REF ;		// TODO: this will need to be expanded to include multiple '.'
OBJECT_REF	:	[A-Z_]+ ;
FN_NAME		: 	[a-zA-Z]+ ;
COMMENT		:	'/*' .*? '*/' -> skip;
WS			:	[ \t\r\n]+ -> skip ;	// skip spaces, tabs, newlines, \r (Windows)
