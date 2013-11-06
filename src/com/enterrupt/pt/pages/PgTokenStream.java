package com.enterrupt.pt.pages;

import java.util.*;
import com.enterrupt.pt.*;
import com.enterrupt.runtime.*;

public class PgTokenStream {

	private Page p;
	private int cursor;
	private PgTokenStream pfs;
	private boolean doReadFromPfs;
	private boolean isClosed;
	private int prevOCCURSLEVEL = -1;
	private HashMap<String, Boolean> loadedPageNames;

	public PgTokenStream(String PNLNAME) {
		this.p = DefnCache.getPage(PNLNAME);
		this.loadedPageNames = new HashMap<String, Boolean>();
	}

	public PgToken next() {

		// If the end of page token has been emitted, only emit null.
		if(isClosed) {
			return null;
		}

		// If a nested page stream is active, read from that stream.
		if(doReadFromPfs) {
			return this.readFromPfs();
		}

		if(this.cursor < p.tokens.size()) {
			PgToken tok = p.tokens.get(this.cursor++);

			// Changes in scroll level must be reported to the receiver.
			if(tok.OCCURSLEVEL < this.prevOCCURSLEVEL) {
				tok.flags.add(AFlag.SCROLL_LVL_DECREMENT);
			}

			// If this is a subpage or secpage, expand it in-place to its constituent tokens.
			if(tok.flags.contains(AFlag.PAGE)) {
				if(this.loadedPageNames.get(tok.SUBPNLNAME) == null) {
					this.pfs = new PgTokenStream(tok.SUBPNLNAME);
					this.loadedPageNames.put(tok.SUBPNLNAME, true);
					this.doReadFromPfs = true;
				} else {
					this.prevOCCURSLEVEL = tok.OCCURSLEVEL; // Must save previous OCCURSLEVEL before returning.
					return this.next();
				}
			}

			// If this is a scroll bar / grid / scroll area, find its primary record name.
			if(tok.flags.contains(AFlag.SCROLL_START)) {

				int lookAheadCursor = this.cursor;
				boolean foundPrimaryRecName = false;

				while(lookAheadCursor < p.tokens.size()) {
					PgToken lookToken = p.tokens.get(lookAheadCursor++);

					// Ignore groupboxes while trying to find the primary record name.
					if(!lookToken.flags.contains(AFlag.GROUPBOX)) {
						tok.primaryRecName = lookToken.RECNAME;
						foundPrimaryRecName = true;
						break;
					}
				}

				if(!foundPrimaryRecName) {
					throw new EntVMachRuntimeException("Unable to find the scroll area's primary record name.");
				}
			}

			/**
			 * If this token is not a page, but it has a SUBPNLNAME, preemptively emit it in the stream.
			 * This token should be emitted immediately after the preemptively loaded page, so remind the cursor
			 * by 1 to ensure that it gets picked up again later.
			 */
			if(tok.SUBPNLNAME.length() > 0 && !tok.flags.contains(AFlag.PAGE)) {
				if(this.loadedPageNames.get(tok.SUBPNLNAME) == null) {
					this.pfs = new PgTokenStream(tok.SUBPNLNAME);
					this.loadedPageNames.put(tok.SUBPNLNAME, true);
					this.doReadFromPfs = true;

					this.cursor--;		// This token will be picked up again after page stream has ended.

					/**
					 * Emit a new PAGE token so that the caller knows a sub page is being emitted.
					 * The current token will be emitted after the sub page has finished streaming.
					 * Note that prevOCCURSLEVEL is not saved before returning here; we're going to pick
					 * up where we left off later so the prevOCCURSLEVEL must not be changed.
				 	 */
					PgToken preemptivePgToken = new PgToken(AFlag.PAGE);
					preemptivePgToken.SUBPNLNAME = tok.SUBPNLNAME;
					return preemptivePgToken;
				}
			}

			this.prevOCCURSLEVEL = tok.OCCURSLEVEL;
			return tok;

		} else {
			// Before null is emitted, tell the receiver that this page has ended.
			PgToken endTok = new PgToken();
			endTok.flags.add(AFlag.END_OF_PAGE);
			this.isClosed = true;
			return endTok;
		}
	}

	public PgToken readFromPfs() {

		PgToken tok = this.pfs.next();
		if(tok == null) {
			this.doReadFromPfs = false;
			tok = this.next();
		}
		return tok;
	}
}
