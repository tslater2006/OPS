package com.enterrupt;

import com.enterrupt.sql.*;
import com.enterrupt.pt_objects.*;

public class Main {

    public static void main(String[] args) {

		try {
			StmtLibrary.init();
			BuildAssistant.init();
			PCParser.init();

			RunTimeEnvironment.init();
			RunTimeEnvironment.SYSVAR_EmployeeId = "AA0001";
			RunTimeEnvironment.SYSVAR_Menu = "SA_LEARNER_SERVICES";

			Component c = new Component("SSS_STUDENT_CENTER", "GBL");
			c.loadInitialMetadata();

			Menu m = new Menu("SA_LEARNER_SERVICES");
			m.loadInitialMetadata();

			c.loadSearchRecord();

			c.getListOfComponentPC();
			c.loadAndRunRecordPConSearchRecord();
			c.loadAndRunComponentPConSearchRecord();

			for(String pnlname : c.pages) {
				recurseSubPagesOf(pnlname, 0);
			}

			for(String pnlname : c.pages) {
				recurseSecPagesOf(pnlname, 0);
			}

			StmtLibrary.disconnect();
			BuildAssistant.printInfo();
			BuildAssistant.verifyAgainstTraceFile();
		} catch(Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
    }

	private static void recurseSubPagesOf(String pnlname, int indent) throws Exception {

		Page p = BuildAssistant.pageDefnCache.get(pnlname);
		if(p == null) {
			p = new Page(pnlname);
			p.loadInitialMetadata();
			BuildAssistant.cachePage(p);
		}
		for(String subpnlname : p.subpages) {
			recurseSubPagesOf(subpnlname, indent+1);
		}
	}

	private static void recurseSecPagesOf(String pnlname, int indent) throws Exception {

		Page p = BuildAssistant.pageDefnCache.get(pnlname);
		if(p == null) {
			p = new Page(pnlname);
			p.loadInitialMetadata();
			BuildAssistant.cachePage(p);
		}

		// Recursively expand/search subpages for secpages.
		for(String subpnlname : p.subpages) {
			recurseSecPagesOf(subpnlname, indent+1);
		}

		// Then, recursively expand/search secpages for more secpages.
		for(String secpnlname : p.secpages) {
			recurseSecPagesOf(secpnlname, indent+1);
		}
	}
}
