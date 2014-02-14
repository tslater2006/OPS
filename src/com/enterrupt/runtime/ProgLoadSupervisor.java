package com.enterrupt.runtime;

import java.io.*;
import java.util.*;
import com.enterrupt.antlr4.*;
import com.enterrupt.runtime.*;
import org.apache.logging.log4j.*;
import com.enterrupt.pt.peoplecode.*;
import org.antlr.v4.runtime.tree.*;

public class ProgLoadSupervisor {

	/**
	 * It appears that the defn and program loading process differs based
	 * on the purpose of the load; if the defns being loaded are about to
	 * be interpreted, more defns and progs must be loaded than usual. Use DEEP
	 * if a program being loaded is about to be run, and SHALLOW if the program won't
	 * be run immediately.
	 */
	public LoadGranularity loadGranularity;

	private PeopleCodeProg rootProg;
	private boolean writeToFile = true;
	public Stack<PeopleCodeProg> loadStack;

	private Logger log = LogManager.getLogger(ProgLoadSupervisor.class.getName());

	public ProgLoadSupervisor(PeopleCodeProg prog, LoadGranularity lg) {
		this.rootProg = prog;
		this.loadGranularity = lg;
		this.loadStack = new Stack<PeopleCodeProg>();
	}

	public void start() {
		this.loadStack.push(this.rootProg);
		this.loadTopOfStack();
		if(this.loadStack.size() != 0) {
			throw new EntVMachRuntimeException("Expected load stack to be empty.");
		}
	}

	public void loadImmediately(PeopleCodeProg prog) {
		this.loadStack.push(prog);
		this.loadTopOfStack();
	}

	public void loadTopOfStack() {

		//log.debug("ProgLoadSupervisor stack: {}", this.loadStack);

		PeopleCodeProg prog = this.loadStack.peek();
		if(prog.haveDefnsAndProgsBeenLoaded()) {
			this.loadStack.pop();
			return;
		}

		prog.lexAndParse();
		int recurseLvl = loadStack.size() - 1;

		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(new ProgLoadListener(prog, this,
			prog.tokenStream), prog.parseTree);

	    /**
    	 * All programs referenced by this program must have their referenced
         * definitions and programs loaded now.
         */
		for(PeopleCodeProg refProg : prog.referencedProgs) {
           	refProg = DefnCache.getProgram(refProg);
	        refProg.init();

			/**
			 * In Record PC mode, referenced defns and progs should be loaded
			 * recursively up to three levels deep. In Component PC mode, all App
			 * Package PC programs must be be permitted to load their references
			 * recursively with no limit; for all other program types, their
			 * references should be loaded only if they are directly referenced in
			 * the root Component PC program being loaded (recursion level 0).
			 */
	        if(((this.rootProg instanceof RecordPeopleCodeProg
					|| this.rootProg instanceof PagePeopleCodeProg) && recurseLvl < 3)
               	|| (this.rootProg instanceof ComponentPeopleCodeProg
	                && (refProg instanceof AppClassPeopleCodeProg
						|| recurseLvl == 0))) {

				/**
				 * If the program is never actually called, there is
			   	 * no reason to load its references at this time.
				 */
				if(refProg instanceof RecordPeopleCodeProg &&
					prog.confirmedRecordProgCalls.get(refProg) == null) {
					continue;
				}

				loadStack.push(refProg);
				this.loadTopOfStack();
            }
        }

		loadStack.pop();
	}
}
