package com.enterrupt.pt_objects;

public class RecordField {

	public String FIELDNAME;
	public byte USEEDIT;

	public int posInRecord;
	private final byte KEY_FLAG = (byte) 1;

	public RecordField() {}

	public boolean isKey() {
		return ((this.USEEDIT & this.KEY_FLAG) > 0);
	}
}
