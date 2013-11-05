package com.enterrupt;

import com.enterrupt.pt_objects.*;
import java.util.HashMap;

public class DefnCache {

	private static HashMap<String, Record> records;
	private static HashMap<String, PeopleCodeProg> programs;

	static {
		records = new HashMap<String, Record>();
		programs = new HashMap<String, PeopleCodeProg>();
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
}
