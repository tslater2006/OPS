package com.enterrupt.antlr4;

import org.antlr.v4.runtime.*;
import com.enterrupt.antlr4.frontend.*;
import com.enterrupt.runtime.*;

public class NoErrorTolerancePeopleCodeLexer extends PeopleCodeLexer {

	public NoErrorTolerancePeopleCodeLexer(CharStream input) {
		super(input);
	}

	public void recover(LexerNoViableAltException e) {
		throw new EntVMachRuntimeException(e.getMessage(), e);
	}
}
