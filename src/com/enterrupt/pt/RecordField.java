package com.enterrupt.pt;

public class RecordField {

	public String RECNAME;
	public String FIELDNAME;
	public byte USEEDIT;
	public int FIELDNUM;

	private final byte KEY_FLAG = (byte) 1;
	private final byte ALTERNATE_SEARCH_KEY_FLAG = (byte) 16;
	private final byte SEARCH_KEY_FLAG = (byte) 2048;

	public RecordField() {}

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
