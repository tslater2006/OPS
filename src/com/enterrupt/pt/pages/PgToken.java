package com.enterrupt.pt.pages;

import java.util.EnumSet;
import com.enterrupt.pt.*;
import com.enterrupt.runtime.*;

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

	public boolean doesBelongInComponentStructure() {

		// If RECNAME or FIELDNAME is empty, don't add.
		if(this.RECNAME.length() == 0 || this.FIELDNAME.length() == 0) {
			return false;
		}

		// Related display fields should not be added.
		if((this.FIELDUSE & this.REL_DISP_FLAG) > 0) {
			return false;
		}

		/**
		 * TODO: Keep an eye on these two checks involving subrecords.
		 * It may be necessary to resolve subrecord fields to their parent/gp/etc.,
		 * rather than simply waiting for records to be expanded by the component buffer.
		 */
		// Subrecords should not be added.
		Record recDefn = DefnCache.getRecord(this.RECNAME);
		if(recDefn.isSubrecord()) {
			return false;
		}

		/**
		 * Even if RECNAME does not point to a subrecord, this field
		 * may point to a subrecord on the record pointed to by RECNAME,
		 * in which case it will not be present in the immediate record.
	 	 * If that's the case, this field should not be added.
		 */
		if(recDefn.fieldTable.get(this.FIELDNAME) == null) {
			return false;
		}

		return true;
	}
}
