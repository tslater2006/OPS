package com.enterrupt;

import com.enterrupt.sql.*;
import com.enterrupt.pt.*;
import com.enterrupt.types.*;
import com.enterrupt.runtime.*;
import org.apache.logging.log4j.*;

public class Main {

	private static Logger log = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {

		try {

			Runtime.getRuntime().addShutdownHook(new ENTShutdownHook());

			RunTimeEnvironment.setSystemVar("%EmployeeId", "AA0001");
			RunTimeEnvironment.setSystemVar("%Menu", "SA_LEARNER_SERVICES");
			RunTimeEnvironment.setSystemVar("%OperatorId", "KADAMS");

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
