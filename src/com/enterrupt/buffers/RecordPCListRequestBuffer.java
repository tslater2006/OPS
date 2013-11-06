package com.enterrupt.buffers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Stack;
import com.enterrupt.DefnCache;
import com.enterrupt.pt_objects.*;

/**
 * This class abstracts away the process of expanding secpages
 * in place within a token stream. This process is necessary
 * in order to generate the correct sequence of SQL requests for
 * retrieving the list of Record PC for all records in a component,
 * which occurs while PeopleTools gathers metadata about pages, their fields,
 * and those fields' associated record definitions in a recursive fashion.
 */
public class RecordPCListRequestBuffer {

	private static LinkedList<PgToken> tokenStream;
	private static LinkedList<PgToken> expandingStream;
	private static HashMap<String, Boolean> flushedRecordNames;
	private static Stack<PgToken> secpagesBeingExpanded;

	public static void init() {
		tokenStream = new LinkedList<PgToken>();
		expandingStream = new LinkedList<PgToken>();
		flushedRecordNames = new HashMap<String, Boolean>();
		secpagesBeingExpanded = new Stack<PgToken>();
	}

	public static void queueFieldToken(PgToken tok) {

		// If the token's RECNAME has already been flushed, there's no need to add it to the stream.
		if(tok.RECNAME != null && tok.RECNAME.length() > 0
			&& flushedRecordNames.get(tok.RECNAME) == null) {

			expandingStream.add(tok);
		}
	}

	public static void queueSecpageToken(PgToken tok) {
		expandingStream.add(tok);
	}

	public static void notifyStartOfExpansion(PgToken secpageTok) {

		if(secpagesBeingExpanded.size() == 0) {

			// If no secpages are being expanded, just add tokens to the end of the stream.
			tokenStream.addAll(expandingStream);
		} else {

			/**
			 * If one or more secpages are being expanded, we must write the recently
			 * expanded tokens *before* the secpage token's occurrence in the stream.
			 */
			PgToken expandingTok = secpagesBeingExpanded.peek();
			int idx = tokenStream.indexOf(expandingTok);
			tokenStream.addAll(idx, expandingStream);
		}
		expandingStream.clear();
		secpagesBeingExpanded.push(secpageTok);
	}

	public static void notifyEndOfExpansion(PgToken secpageTok) {

		secpagesBeingExpanded.pop();

		/**
		 * Now that the secpage has been completely expanded, we can
		 * replace its token in the stream with the recently expanded tokens.
		 * Once the secpage token has been removed, the tokens added when expansion
		 * began are seamlessly in order with the tokens added here.
		 */
		int idx = tokenStream.indexOf(secpageTok);
		tokenStream.addAll(idx, expandingStream);
		tokenStream.remove(secpageTok);
		expandingStream.clear();
	}

	public static void flushUpTo(PgToken secpageTok) {
		flush(secpageTok);
	}

	/**
	 * This should be run after all pages have been traversed
	 * to ensure that all requests are flushed.
	 */
	public static void flushEntireTokenStream() {
		flush(null);
	}

	private static void flush(PgToken tok) {
		Iterator<PgToken> iter = tokenStream.listIterator();
		while(iter.hasNext()) {
			PgToken t = iter.next();

			if(t == tok) {
				break;
			}

			if(t.flags.contains(AFlag.SECPAGE)) {
				continue;
			}

			issuePCListRequestForRecord(t.RECNAME);
			iter.remove();
		}
	}

	private static void issuePCListRequestForRecord(String RECNAME) {

		// If the record is a system table, we can ignore it.
		if(flushedRecordNames.get(RECNAME) == null &&
				!PSDefn.isSystemRecord(RECNAME)) {

			Record recDefn = DefnCache.getRecord(RECNAME);
			recDefn.discoverRecordPC();
			flushedRecordNames.put(RECNAME, true);

			/**
			 * If this record contains subrecords, requests for their
			 * Record PC listings should be issued now.
	 		 */
			for(String subrecname : recDefn.subRecordNames) {
				issuePCListRequestForRecord(subrecname);
			}
		}
	}
}
