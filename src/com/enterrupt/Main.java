package com.enterrupt;

import com.enterrupt.sql.*;
import com.enterrupt.pt.*;
import com.enterrupt.types.*;
import com.enterrupt.runtime.*;
import com.enterrupt.buffers.*;
import org.apache.logging.log4j.*;

public class Main {

	private static Logger log = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {

		try {

			Runtime.getRuntime().addShutdownHook(new ENTShutdownHook());

			Environment.setSystemVar("%Component", System.getProperty("ComponentToLoad"));
			Environment.setSystemVar("%Menu", "SA_LEARNER_SERVICES");
			Environment.setSystemVar("%OperatorId", "KADAMS");
			Environment.setSystemVar("%EmployeeId", "AA0001");

			Component c = new Component(
				(String)Environment.getSystemVar("%Component").read(), "GBL");
			Menu m = new Menu(
				(String)Environment.getSystemVar("%Menu").read());

			c.getListOfComponentPC();
			c.loadAndRunRecordPConSearchRecord();
			c.loadAndRunComponentPConSearchRecord();
			c.fillSearchRecord();

			c.loadPages();

			c.assembleComponentStructure();
			ComponentBuffer.printStructure();
			ComponentStructureVerifier.verify();
			TraceFileVerifier.logVerificationSummary(false);

		} catch(EntVMachRuntimeException evmre) {
			log.fatal(evmre.getMessage(), evmre);
			TraceFileVerifier.logVerificationSummary(true);
			System.exit(ExitCode.ENT_VIRTUAL_MACH_RUNTIME_EXCEPTION.getCode());
		} catch(Exception ex) {
			log.fatal(ex.getMessage(), ex);
			TraceFileVerifier.logVerificationSummary(true);
			System.exit(ExitCode.GENERIC_FAILURE.getCode());
		}
    }

	private static class ENTShutdownHook extends Thread {
		public void run() {
			StmtLibrary.disconnect();
			TraceFileVerifier.closeTraceFile();
		}
	}
}
