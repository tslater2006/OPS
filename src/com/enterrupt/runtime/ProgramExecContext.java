package com.enterrupt.runtime;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.runtime.*;

public class ProgramExecContext extends ExecContext {

	public String funcName;
	public ParseTree funcStartNode;

	public ProgramExecContext(PeopleCodeProg p) {
		super(p);
	}

	public ProgramExecContext(PeopleCodeProg p, String f) {
		super(p);
		this.funcName = f;

		/**
		 * TODO: Resolve function name to parse tree node.
		 * Note that execution must start with the program grammar rule
		 * in order to allocate ProgramLocal variables. Once a function declaration
	 	 * is encountered, the interpreter will need to stop visiting the entire program
		 * and jump directly to the parse tree node pointed to by funcStartNode.
		 */
		throw new EntVMachRuntimeException("Running external functions is not yet " +
			"supported.");
	}
}