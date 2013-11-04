package com.enterrupt;

import com.enterrupt.pt_objects.*;
import java.util.HashMap;

public class DefnCache {

	private static HashMap<String, Record> records;

	static {
		records = new HashMap<String, Record>();
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
			r.loadInitialMetadata();
			records.put(RECNAME, r);
		}
		return r;
	}
}
