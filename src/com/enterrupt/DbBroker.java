package com.enterrupt;

class DbBroker {

    public static void main(String[] args) {

		StmtLibrary.init();
		BuildAssistant.init();

		Component c = new Component("SSS_STUDENT_CENTER", "GBL");
		c.loadInitialMetadata();

		Menu m = new Menu("SA_LEARNER_SERVICES");
		m.loadInitialMetadata();

		c.loadSearchRecord();

		for(String pnlname : c.pages) {
			recurseSubPagesOf(pnlname, 0);
		}

		for(String pnlname : c.pages) {
			recurseSecPagesOf(pnlname, 0);
		}

		StmtLibrary.disconnect();
		BuildAssistant.printInfo();
		BuildAssistant.verifyAgainstTraceFile();
    }

	private static void recurseSubPagesOf(String pnlname, int indent) {

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

	private static void recurseSecPagesOf(String pnlname, int indent) {

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
