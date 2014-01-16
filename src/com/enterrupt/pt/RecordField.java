package com.enterrupt.pt;

import org.apache.logging.log4j.*;
import com.enterrupt.runtime.*;
import com.enterrupt.types.*;

public class RecordField {

	public String RECNAME;
	public String FIELDNAME;
	public int USEEDIT;
	public int FIELDNUM;
	private int typeFlag;

	private final int KEY_FLAG = 1;
	private final int ALTERNATE_SEARCH_KEY_FLAG = 16;
	private final int DESCENDING_KEY_FLAG = 64;
	private final int SEARCH_KEY_FLAG = 2048;

	private static Logger log = LogManager.getLogger(RecordField.class.getName());

	public RecordField(String r, String f, int t) {
		this.RECNAME = r;
		this.FIELDNAME = f;
		this.typeFlag = t;
	}

	public PTType getSentinelForUnderlyingValue() {
		switch(this.typeFlag) {
			case 0:
                return PTString.getSentinel();
            case 2:
                return PTNumber.getSentinel();
            case 4:
                return PTDate.getSentinel();
            default:
				throw new EntVMachRuntimeException("Unable to determine " +
					"appropriate sentinel for underlying record field " +
					"value given a typeFlag of: " + this.typeFlag);
		}
	}

	public boolean isKey() {
		return ((this.USEEDIT & this.KEY_FLAG) > 0);
	}

	public boolean isDescendingKey() {
		return ((this.USEEDIT & this.DESCENDING_KEY_FLAG) > 0);
	}

	public boolean isSearchKey() {
		return ((this.USEEDIT & this.SEARCH_KEY_FLAG) > 0);
	}

	public boolean isAlternateSearchKey() {
		return ((this.USEEDIT & this.ALTERNATE_SEARCH_KEY_FLAG) > 0);
	}
}

