package com.enterrupt.pt_objects;

import java.util.EnumSet;

public class PgToken {

	public EnumSet<AFlag> flags;
	public String RECNAME;
	public String FIELDNAME;
	public String SUBPNLNAME;
	public int OCCURSLEVEL;
	public byte FIELDUSE;

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
}
