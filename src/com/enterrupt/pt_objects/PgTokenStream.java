package com.enterrupt.pt_objects;

import com.enterrupt.BuildAssistant;

public class PgTokenStream {

	private Page p;
	private int cursor;
	private PgTokenStream pfs;
	private boolean doReadFromPfs;

	public PgTokenStream(String PNLNAME) throws Exception {
		this.p = BuildAssistant.getLoadedPage(PNLNAME);
	}

	public PgToken next() throws Exception {

		if(doReadFromPfs) {
			return this.readFromPfs();
		}

		if(this.cursor < p.tokens.size()) {
			PgToken tok = p.tokens.get(this.cursor++);

			if(tok.flags.contains(AFlag.SUBPAGE) ||
				tok.flags.contains(AFlag.SECPAGE)) {

				this.pfs = new PgTokenStream(tok.SUBPNLNAME);
				this.doReadFromPfs = true;
				return this.readFromPfs();
			}
			return tok;
		}
		return null;
	}

	public PgToken readFromPfs() throws Exception {
		PgToken tok = this.pfs.next();
		if(tok == null) {
			this.doReadFromPfs = false;
			tok = this.next();
		}
		return tok;
	}
}
