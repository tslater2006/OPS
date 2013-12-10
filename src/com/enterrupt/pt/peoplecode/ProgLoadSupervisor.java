package com.enterrupt.pt.peoplecode;

import java.io.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import com.enterrupt.antlr4.*;
import com.enterrupt.antlr4.frontend.*;
import org.antlr.v4.runtime.atn.PredictionMode;
import com.enterrupt.runtime.*;
import java.util.*;
import org.apache.logging.log4j.*;

public class ProgLoadSupervisor {

	private PeopleCodeProg rootProg;
	private Stack<PeopleCodeProg> loadStack;
	private LFlag lflag;
	private boolean writeToFile = true;

	private Logger log = LogManager.getLogger(ProgLoadSupervisor.class.getName());

	public ProgLoadSupervisor(PeopleCodeProg prog) {
		this.rootProg = prog;
		this.loadStack = new Stack<PeopleCodeProg>();

        if(prog instanceof ComponentPeopleCodeProg) {
            this.lflag = LFlag.COMPONENT;
        } else if(prog instanceof PagePeopleCodeProg || prog instanceof RecordPeopleCodeProg) {
            this.lflag = LFlag.RECORD;
        } else {
            throw new EntVMachRuntimeException("Unexpected type of root PeopleCode.");
        }
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

		PeopleCodeProg prog = this.loadStack.peek();
		if(prog.haveDefnsAndProgsBeenLoaded()) {
			this.loadStack.pop();
			return;
		}

		try {
	        InputStream progTextInputStream =
			    new ByteArrayInputStream(prog.parsedText.getBytes());

            log.debug("=== ProgLoadSupervisor =============================");
			log.debug("Loading {}", prog.getDescriptor());
/*			String[] lines = prog.parsedText.split("\n");
			for(int i = 0; i < lines.length; i++) {
	            log.debug("{}:\t{}", i+1, lines[i]);
    		}*/

            if(this.writeToFile) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(
                    new File("/home/mquinn/evm/cache/" + prog.getDescriptor() + ".pc")));
                writer.write(prog.parsedText);
                writer.close();
            }

        	ANTLRInputStream input = new ANTLRInputStream(progTextInputStream);
	        NoErrorTolerancePeopleCodeLexer lexer = new NoErrorTolerancePeopleCodeLexer(input);
    	    CommonTokenStream tokens = new CommonTokenStream(lexer);
        	PeopleCodeParser parser = new PeopleCodeParser(tokens);

	        parser.removeErrorListeners();
	        parser.addErrorListener(new EntDiagErrorListener());
    	    parser.getInterpreter()
	    	    .setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
			parser.setErrorHandler(new EntErrorStrategy());

	        ParseTree tree = parser.program();

			log.debug(">>> Parse Tree >>>>>>>>>>>>");
			if(prog instanceof AppClassPeopleCodeProg) {
				log.debug(tree.toStringTree(parser));
			}
	        log.debug("====================================================");

            if(this.writeToFile) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(
                    new File("/home/mquinn/evm/cache/" + prog.getDescriptor() + ".tree")));
                writer.write(tree.toStringTree(parser));
                writer.close();
            }

			ParseTreeWalker walker = new ParseTreeWalker();

			int recurseLvl = loadStack.size() - 1;
			walker.walk(new ProgLoadListener(prog, recurseLvl, this, tokens), tree);

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
	        	if((this.rootProg instanceof RecordPeopleCodeProg && recurseLvl < 3)
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
	    } catch(java.io.IOException ioe) {
            throw new EntVMachRuntimeException(ioe.getMessage());
        }

		loadStack.pop();
	}
}
