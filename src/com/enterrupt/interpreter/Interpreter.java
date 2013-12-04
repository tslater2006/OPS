package com.enterrupt.interpreter;

import java.util.Stack;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
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

			log.warn("====================================================");
			log.warn(this.prog.parsedText);
			log.warn("====================================================");

	        ANTLRInputStream input = new ANTLRInputStream(progTextInputStream);
	        PeopleCodeLexer lexer = new PeopleCodeLexer(input);
	        CommonTokenStream tokens = new CommonTokenStream(lexer);
	        PeopleCodeParser parser = new PeopleCodeParser(tokens);

			parser.removeErrorListeners();
			parser.addErrorListener(new EntDiagErrorListener());
			parser.getInterpreter()
				.setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);

    	    ParseTree tree = parser.program();
			InterpreterVisitor interpreter = new InterpreterVisitor();
			interpreter.visit(tree);

        	System.out.println(tree.toStringTree(parser));

		} catch(java.io.IOException ioe) {
			throw new EntVMachRuntimeException(ioe.getMessage());
		}
	}
}
