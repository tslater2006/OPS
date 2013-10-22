package com.enterrupt;

import com.enterrupt.sql.*;
import com.enterrupt.pt_objects.*;
import com.enterrupt.types.*;
import com.enterrupt.parser.Parser;
import com.enterrupt.interpreter.RunTimeEnvironment;

public class Main {

    public static void main(String[] args) {

		try {
			StmtLibrary.init();
			BuildAssistant.init();
			Parser.init();
			ComponentBuffer.init();

			RunTimeEnvironment.init();
			((StringPtr)RunTimeEnvironment.systemVarTable.get("%EmployeeId")).systemWrite("AA0001");
			((StringPtr)RunTimeEnvironment.systemVarTable.get("%Menu")).systemWrite("SA_LEARNER_SERVICES");
			((StringPtr)RunTimeEnvironment.systemVarTable.get("%OperatorId")).systemWrite("KADAMS");

			// TODO: Remove, should be generated during SQL processing.
			RunTimeEnvironment.compBufferTable.put("LS_SS_PERS_SRCH.EMPLID", new StringPtr());

			Component c = new Component("SSS_STUDENT_CENTER", "GBL");
			c.loadInitialMetadata();

			Menu m = new Menu("SA_LEARNER_SERVICES");
			m.loadInitialMetadata();

			c.loadSearchRecord();

			c.getListOfComponentPC();
			c.loadAndRunRecordPConSearchRecord();
			c.loadAndRunComponentPConSearchRecord();
			c.fillSearchRecord();

			c.loadPages();

			c.assembleComponentStructure();

			StmtLibrary.disconnect();
			BuildAssistant.printInfo();
			BuildAssistant.verifyAgainstTraceFile();
		} catch(Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
    }
}
