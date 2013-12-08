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
			String[] lines = prog.parsedText.split("\n");
			for(int i = 0; i < lines.length; i++) {
	            log.debug("{}:\t{}", i+1, lines[i]);
    		}

            if(this.writeToFile) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(
                    new File("/home/mquinn/evm/cache/" + prog.getDescriptor() + ".pc")));
                writer.write(prog.parsedText);
                writer.close();
            }

        	ANTLRInputStream input = new ANTLRInputStream(progTextInputStream);
	        PeopleCodeLexer lexer = new PeopleCodeLexer(input);
    	    CommonTokenStream tokens = new CommonTokenStream(lexer);
        	PeopleCodeParser parser = new PeopleCodeParser(tokens);

	        parser.removeErrorListeners();
	        parser.addErrorListener(new EntDiagErrorListener());
    	    parser.getInterpreter()
	    	    .setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
			parser.setErrorHandler(new EntErrorStrategy());

	        ParseTree tree = parser.program();

			log.debug(">>> Parse Tree >>>>>>>>>>>>");
            log.debug(tree.toStringTree(parser));
	        log.debug("====================================================");

            if(this.writeToFile) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(
                    new File("/home/mquinn/evm/cache/" + prog.getDescriptor() + ".tree")));
                writer.write(tree.toStringTree(parser));
                writer.close();
            }

	        ProgLoaderVisitor progLoader = new ProgLoaderVisitor();
    	    progLoader.visit(tree);

	    } catch(java.io.IOException ioe) {
            throw new EntVMachRuntimeException(ioe.getMessage());
        }

		loadStack.pop();
	}
}
