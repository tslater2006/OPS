package com.enterrupt.pt;

import org.apache.logging.log4j.*;

public class RecordField {

	public String RECNAME;
	public String FIELDNAME;
	public byte USEEDIT;
	public int FIELDNUM;
	public int typeFlag;

	private final byte KEY_FLAG = (byte) 1;
	private final byte ALTERNATE_SEARCH_KEY_FLAG = (byte) 16;
	private final byte SEARCH_KEY_FLAG = (byte) 2048;

	private static Logger log = LogManager.getLogger(RecordField.class.getName());

	public RecordField(String r, String f, int t) {
		this.RECNAME = r;
		this.FIELDNAME = f;
		this.typeFlag = t;
	}

	public boolean isKey() {
		return ((this.USEEDIT & this.KEY_FLAG) > 0);
	}

	public boolean isSearchKey() {
		return ((this.USEEDIT & this.SEARCH_KEY_FLAG) > 0);
	}

	public boolean isAlternateSearchKey() {
		return ((this.USEEDIT & this.ALTERNATE_SEARCH_KEY_FLAG) > 0);
	}
}
