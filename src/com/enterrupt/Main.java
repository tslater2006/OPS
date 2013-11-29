package com.enterrupt;

import com.enterrupt.sql.*;
import com.enterrupt.pt.*;
import com.enterrupt.types.*;
import com.enterrupt.runtime.*;
import com.enterrupt.parser.Parser;
import com.enterrupt.buffers.ComponentBuffer;
import org.apache.logging.log4j.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import com.enterrupt.antlr.*;

public class Main {

	private static Logger log = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {

		try {

		ANTLRInputStream input = new ANTLRInputStream(System.in);
		PeopleCodeLexer lexer = new PeopleCodeLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PeopleCodeParser parser = new PeopleCodeParser(tokens);
		ParseTree tree = parser.r();

		System.out.println(tree.toStringTree(parser));

			Runtime.getRuntime().addShutdownHook(new ENTShutdownHook());

			((StringPtr)RunTimeEnvironment.systemVarTable.get("%EmployeeId")).systemWrite("AA0001");
			((StringPtr)RunTimeEnvironment.systemVarTable.get("%Menu")).systemWrite("SA_LEARNER_SERVICES");
			((StringPtr)RunTimeEnvironment.systemVarTable.get("%OperatorId")).systemWrite("KADAMS");

			// TODO: Remove, should be generated during SQL processing.
			RunTimeEnvironment.compBufferTable.put("LS_SS_PERS_SRCH.EMPLID", new StringPtr());

			Component c = new Component(System.getProperty("ComponentToLoad"), "GBL");
			Menu m = new Menu("SA_LEARNER_SERVICES");

			c.loadSearchRecord();

			c.getListOfComponentPC();
			c.loadAndRunRecordPConSearchRecord();
			c.loadAndRunComponentPConSearchRecord();
			c.fillSearchRecord();

			c.loadPages();

			c.assembleComponentStructure();
			c.loadAllRecordPCProgsAndReferencedDefns();
			c.loadAllComponentPCProgsAndReferencedDefns();
			c.loadAllPagePC();

			BuildAssistant.runValidationTests(c);
		} catch(EntVMachRuntimeException evmre) {
			log.fatal(evmre.getMessage(), evmre);
			System.exit(ExitCode.ENT_VIRTUAL_MACH_RUNTIME_EXCEPTION.getCode());
		} catch(Exception ex) {
			log.fatal(ex.getMessage(), ex);
			System.exit(ExitCode.GENERIC_FAILURE.getCode());
		}
    }

	private static class ENTShutdownHook extends Thread {
		public void run() {
			StmtLibrary.disconnect();
		}
	}
}
