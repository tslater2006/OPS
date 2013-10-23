package com.enterrupt.pt_objects;

import java.util.EnumSet;
import com.enterrupt.BuildAssistant;

public class PgToken {

	public EnumSet<AFlag> flags;
	public String RECNAME;
	public String FIELDNAME;
	public String SUBPNLNAME;
	public int OCCURSLEVEL;
	public byte FIELDUSE;

	private final byte REL_DISP_FLAG = (byte) 16;

	public String primaryRecName; 		// used for SCROLL_CHNG tokens.

	public PgToken() {
		this.flags = EnumSet.noneOf(AFlag.class);
	}

	public PgToken(AFlag flag) {
		this.flags = EnumSet.of(flag);
	}

	public PgToken(EnumSet<AFlag> flagSet) {
		this.flags = EnumSet.copyOf(flagSet);
	}

	public boolean doesBelongInComponentStructure() throws Exception {

		// If RECNAME or FIELDNAME is empty, don't add.
		if(this.RECNAME.length() == 0 || this.FIELDNAME.length() == 0) {
			return false;
		}

		// Related display fields should not be added.
		if((this.FIELDUSE & this.REL_DISP_FLAG) > 0) {
			return false;
		}

		// Subrecords (type 3) should not be added.
		Record recDefn = BuildAssistant.getRecordDefn(this.RECNAME);
		if(recDefn.RECTYPE == 3) {
			return false;
		}

		return true;
	}
}
