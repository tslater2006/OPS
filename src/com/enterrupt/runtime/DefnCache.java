package com.enterrupt;

import com.enterrupt.pt_objects.*;
import java.util.HashMap;

public class DefnCache {

	private static HashMap<String, Record> records;
	private static HashMap<String, PeopleCodeProg> programs;
	private static HashMap<String, Page> pages;
	private static HashMap<String, AppPackage> appPackages;

	static {
		records = new HashMap<String, Record>();
		programs = new HashMap<String, PeopleCodeProg>();
		pages = new HashMap<String, Page>();
		appPackages = new HashMap<String, AppPackage>();
	}

	public static Record getRecord(String RECNAME) throws Exception {

		// Ignore requests for system records like PSXlATITEM.
		if(RECNAME == null || RECNAME.length() == 0
			|| PSDefn.isSystemRecord(RECNAME)) {
			return null;
		}

		Record r = records.get(RECNAME);
		if(r == null) {
			r = new Record(RECNAME);
			r.init();
			records.put(RECNAME, r);
		}
		return r;
	}

	public static Page getPage(String PNLNAME) throws Exception {

		if(PNLNAME.length() == 0) {
			return null;
		}

		Page p = pages.get(PNLNAME);
		if(p == null) {
			p = new Page(PNLNAME);
			p.init();
			pages.put(p.PNLNAME, p);
		}
		return p;
	}

	public static AppPackage getAppPackage(String packageName) throws Exception {

        AppPackage ap = appPackages.get(packageName);
        if(ap == null) {
            ap = new AppPackage(packageName);
            ap.discoverAppClassPC();
            appPackages.put(ap.packageName, ap);
        }
        return ap;
	}

	public static PeopleCodeProg getProgram(PeopleCodeProg prog) {

		PeopleCodeProg p = programs.get(prog.getDescriptor());
		if(p == null) {
			p = prog;
			programs.put(p.getDescriptor(), p);
		}
		return p;
	}
}
