package com.enterrupt.interpreter;

import java.util.Stack;
import java.io.*;
import java.nio.charset.Charset;
import com.enterrupt.parser.*;
import com.enterrupt.runtime.*;
import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.types.MemoryPtr;
import org.apache.logging.log4j.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.*;
import com.enterrupt.antlr4.*;
import com.enterrupt.antlr4.frontend.*;

public class Interpreter {

	private PeopleCodeTokenStream stream;
	private PeopleCodeProg prog;
	private static Stack<MemoryPtr>	callStack;
	private static boolean writeToFile = true;

	private static Logger log = LogManager.getLogger(Interpreter.class.getName());

	static {
		callStack = new Stack<MemoryPtr>();
	}

	public Interpreter(PeopleCodeProg prog) {
		this.stream = new PeopleCodeTokenStream(prog);
		this.prog = prog;
	}

	public static void pushToCallStack(MemoryPtr p) {
		log.debug("Push\tCallStack\t" + (p == null ? "null" : p.flags.toString()));
		callStack.push(p);
	}

	public static MemoryPtr popFromCallStack() {
		MemoryPtr p = callStack.pop();
		log.debug("Pop\tCallStack\t\t" + (p == null ? "null" : p.flags.toString()));
		return p;
	}

	public static MemoryPtr peekAtCallStack() {
		return callStack.peek();
	}

	public void run() {

		try {
			InputStream progTextInputStream =
				new ByteArrayInputStream(this.prog.parsedText.getBytes());

            log.debug("=== Interpreter =============================");
            log.debug("Interpreting {}", prog.getDescriptor());
            String[] lines = this.prog.parsedText.split("\n");
            for(int i = 0; i < lines.length; i++) {
                log.debug("{}:\t{}", i+1, lines[i]);
            }

			if(this.writeToFile) {
				BufferedWriter writer = new BufferedWriter(new FileWriter(
					new File("/home/mquinn/evm/cache/" + this.prog.getDescriptor() + ".pc")));
				writer.write(this.prog.parsedText);
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

    	    ParseTree tree = parser.program();

	        log.debug(">>> Parse Tree >>>>>>>>>>>>");
            log.debug(tree.toStringTree(parser));
            log.debug("====================================================");

			if(this.writeToFile) {
				BufferedWriter writer = new BufferedWriter(new FileWriter(
					new File("/home/mquinn/evm/cache/" + this.prog.getDescriptor() + ".tree")));
				writer.write(tree.toStringTree(parser));
				writer.close();
			}

			InterpreterVisitor interpreter = new InterpreterVisitor();
			interpreter.visit(tree);

		} catch(java.io.IOException ioe) {
			throw new EntVMachRuntimeException(ioe.getMessage());
		}
	}
}
