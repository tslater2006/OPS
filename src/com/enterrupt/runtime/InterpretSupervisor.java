package com.enterrupt.runtime;

import java.util.*;
import java.io.*;
import java.nio.charset.Charset;
import com.enterrupt.runtime.*;
import com.enterrupt.trace.*;
import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.types.MemoryPtr;
import org.apache.logging.log4j.*;
import com.enterrupt.antlr4.*;

public class InterpretSupervisor {

	private PeopleCodeProg rootProg;
	private LinkedList<RefEnvi> localRefEnviStack;

	private static Logger log = LogManager.getLogger(InterpretSupervisor.class.getName());

	public InterpretSupervisor(PeopleCodeProg prog) {
		this.rootProg = prog;
		this.localRefEnviStack = new LinkedList<RefEnvi>();
	}

	public void run() {

		this.runTopOfStack();

		if(this.localRefEnviStack.size() != 0) {
			throw new EntVMachRuntimeException("Expected ref envi stack to be empty.");
		}

		if(Environment.getCallStackSize() != 0) {
			throw new EntVMachRuntimeException("Expected call stack to be empty.");
		}
	}

	private void runTopOfStack() {

		this.rootProg.lexAndParse();

		String descriptor = this.rootProg.getDescriptor();
		descriptor = descriptor.substring(descriptor.indexOf(".") + 1);

		TraceFileVerifier.submitEmission(new PCStart("00", descriptor));
		TraceFileVerifier.submitEmission(new PCBegin(descriptor, "0", "0"));
		InterpreterVisitor interpreter = new InterpreterVisitor(
			this.rootProg.tokenStream, this);
		interpreter.visit(this.rootProg.parseTree);
	}

	public void pushRefEnvi(RefEnvi r) {
		// Place the ref envi at the front of the linked list.
		this.localRefEnviStack.push(r);
	}

	public void popRefEnvi() {
		// Remove the ref envi at the front of the linked list.
		this.localRefEnviStack.pop();
	}

	public void assignLocalVar(String id, MemoryPtr ptr) {
		// Assign var to topmost ref envi on the stack (front of the linked list).
		this.localRefEnviStack.peekFirst().assignLocalVar(id, ptr);
	}

	public MemoryPtr resolveIdentifierToPtr(String id) {

		/**
		 * Search through the stack of local referencing environments;
		 * most recently pushed referencing environments get first priority,
		 * so search from front of list (stack) to back.
		 */
		for(RefEnvi envi : this.localRefEnviStack) {
			if(envi.isLocalVarDeclared(id)) {
				return envi.resolveLocalVar(id);
			}
		}

		// If id is not in any local ref envis, check the Component envi.
		if(RefEnvi.isComponentVarDeclared(id)) {
			return RefEnvi.resolveComponentVar(id);
		}

		// If id is still not resolved, check the Global envi.
		if(RefEnvi.isGlobalVarDeclared(id)) {
			return RefEnvi.resolveGlobalVar(id);
		}

		throw new EntVMachRuntimeException("Unable to resolve identifier (" +
			id + ") to pointer after checking all referencing environments.");
	}
}
