package com.enterrupt.runtime;

import java.util.Stack;
import java.io.*;
import java.nio.charset.Charset;
import com.enterrupt.runtime.*;
import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.types.MemoryPtr;
import org.apache.logging.log4j.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.*;
import com.enterrupt.antlr4.*;
import com.enterrupt.antlr4.frontend.*;

public class InterpretSupervisor {

	private PeopleCodeProg rootProg;
	private Stack<ExecutionContext> contextStack;
	private boolean writeToFile = true;

	private static Logger log = LogManager.getLogger(InterpretSupervisor.class.getName());

	public InterpretSupervisor(PeopleCodeProg prog) {
		this.rootProg = prog;
		this.contextStack = new Stack<ExecutionContext>();
	}

	public void run() {

		ExecutionContext initialCtx = new ProgramExecContext(this.rootProg);
		this.contextStack.push(initialCtx);
		this.runTopOfStack();

		if(this.contextStack.size() != 0) {
			throw new EntVMachRuntimeException("Expected context stack to be empty.");
		}

		if(Environment.getCallStackSize() != 0) {
			throw new EntVMachRuntimeException("Expected call stack to be empty.");
		}
	}

	private void runTopOfStack() {

		try {
			InputStream progTextInputStream =
				new ByteArrayInputStream(this.rootProg.programText.getBytes());

            log.debug("=== InterpretSupervisor =============================");
            log.debug("Interpreting {}", this.rootProg.getDescriptor());
            String[] lines = this.rootProg.programText.split("\n");
            for(int i = 0; i < lines.length; i++) {
                log.debug("{}:\t{}", i+1, lines[i]);
            }

			if(this.writeToFile) {
				BufferedWriter writer = new BufferedWriter(new FileWriter(
					new File("/home/mquinn/evm/cache/" + this.rootProg.getDescriptor() + ".pc")));
				writer.write(this.rootProg.programText);
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
            log.debug(tree.toStringTree(parser));
            log.debug("====================================================");

			if(this.writeToFile) {
				BufferedWriter writer = new BufferedWriter(new FileWriter(
					new File("/home/mquinn/evm/cache/" + this.rootProg.getDescriptor() + ".tree")));
				writer.write(tree.toStringTree(parser));
				writer.close();
			}

			InterpreterVisitor interpreter = new InterpreterVisitor(this);
			interpreter.visit(tree);

		} catch(java.io.IOException ioe) {
			throw new EntVMachRuntimeException(ioe.getMessage());
		}

		contextStack.pop();
	}
}

class ExecutionContext {

	public ExecutionContext() {}
}

class ProgramExecContext extends ExecutionContext {

	private PeopleCodeProg prog;

	public ProgramExecContext(PeopleCodeProg p) {
		this.prog = p;
	}
}

class FunctionExecContext extends ExecutionContext {}
class GlobalExecContext extends ExecutionContext {}
class ComponentExecContext extends ExecutionContext {}
