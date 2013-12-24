package com.enterrupt.scope;

import com.enterrupt.pt.peoplecode.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import com.enterrupt.types.*;
import com.enterrupt.runtime.*;

public abstract class ExecContext {

	public PeopleCodeProg prog;
	public ParseTree startNode;

	/**
	 * For typical programs (Record, Page, Component), this stack will consist
	 * of a ProgramLocal ref envi at the bottom and an optional FunctionLocal ref
	 * envi at the top. For app class programs, this stack will consist of a reference
	 * to an AppClassObject ref envi at the bottom for the object being modified
	 * and an optional MethodLocal ref envi at the top.
	 */
	public LinkedList<RefEnvi> refEnviStack;

	public ExecContext(PeopleCodeProg p) {
		this.prog = p;
		this.startNode = p.parseTree;
		this.refEnviStack = new LinkedList<RefEnvi>();
	}

	public void pushRefEnvi(RefEnvi r) {
		// Place the ref envi at the front of the linked list.
		this.refEnviStack.push(r);
	}

	public void popRefEnvi() {
		// Remove the ref envi from the front of the linked list.
		this.refEnviStack.pop();
	}

    public void declareLocalVar(String id, MemPointer p) {
        RefEnvi topMostRefEnvi = this.refEnviStack.peekFirst();
		if(!(topMostRefEnvi instanceof LocalRefEnvi)) {
			throw new EntVMachRuntimeException("Attempted to declare a local variable " +
				"when no local referencing environment is available.");
		}
        topMostRefEnvi.declareVar(id, p);
    }

    public MemPointer resolveIdentifier(String id) {

        /**
         * Search through the stack of referencing environments;
         * most recently pushed referencing environments get first priority,
         * so search from front of list (stack) to back.
         */
        for(RefEnvi envi : this.refEnviStack) {
            if(envi.isIdResolvable(id)) {
                return envi.resolveVar(id);
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
