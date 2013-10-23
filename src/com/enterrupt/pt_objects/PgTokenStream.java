package com.enterrupt.pt_objects;

import com.enterrupt.BuildAssistant;
import java.util.ArrayList;

public class PgTokenStream {

	private Page p;
	private int cursor;
	private PgTokenStream pfs;
	private boolean doReadFromPfs;
	private boolean isClosed;
	private int prevOCCURSLEVEL = -1;

	public PgTokenStream(String PNLNAME) throws Exception {
		this.p = BuildAssistant.getLoadedPage(PNLNAME);
	}

	public PgToken next() throws Exception {

		if(isClosed) {
			return null;
		}

		if(doReadFromPfs) {
			return this.readFromPfs();
		}

		if(this.cursor < p.tokens.size()) {
			PgToken tok = p.tokens.get(this.cursor++);

			if(tok.OCCURSLEVEL < this.prevOCCURSLEVEL) {
				tok.flags.add(AFlag.SCROLL_LVL_DECREMENT);
			}

			// If this is a subpage or secpage, expand it in-place to its constituent tokens.
			if(tok.flags.contains(AFlag.PAGE)) {

				this.pfs = new PgTokenStream(tok.SUBPNLNAME);
				this.doReadFromPfs = true;
			}

			// If this is a scroll bar / grid / scroll area, find its primary record name.
			if(tok.flags.contains(AFlag.SCROLL_START)) {

				int lookAheadCursor = this.cursor;
				boolean foundPrimaryRecName = false;

				while(lookAheadCursor < p.tokens.size()) {
					PgToken lookToken = p.tokens.get(lookAheadCursor++);
					if(!lookToken.flags.contains(AFlag.GROUPBOX)) {
						tok.primaryRecName = lookToken.RECNAME;
						foundPrimaryRecName = true;
						break;
					}
				}

				if(!foundPrimaryRecName) {
					System.out.println("[ERROR] Unable to find the primary record name for a scroll area.");
					System.exit(1);
				}
			}

			this.prevOCCURSLEVEL = tok.OCCURSLEVEL;
			return tok;
		} else {
			PgToken endTok = new PgToken();
			endTok.flags.add(AFlag.END_OF_PAGE);
			this.isClosed = true;
			return endTok;
		}
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
