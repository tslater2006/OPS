package com.enterrupt.pt_objects;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import com.enterrupt.BuildAssistant;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.buffers.RecordPCListRequestBuffer;
import java.util.HashMap;
import com.enterrupt.DefnCache;
import org.apache.logging.log4j.*;
import com.enterrupt.runtime.*;

public class Page {

    public String PNLNAME;

    public ArrayList<PgToken> subpages;
    public ArrayList<PgToken> secpages;
	public ArrayList<PgToken> tokens;
	public PeopleCodeProg pageActivateProg = null;

	private static Logger log = LogManager.getLogger(Page.class.getName());

	private boolean hasDiscoveredPagePC = false;

    public Page(String pnlname) {
        this.PNLNAME = pnlname;
	}

	public void init() {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

		try {
	        pstmt = StmtLibrary.getPSPNLDEFN(this.PNLNAME);
   	        rs = pstmt.executeQuery();
	        rs.next(); // Do nothing with record for now.
    	    rs.close();
        	pstmt.close();

	        this.subpages = new ArrayList<PgToken>();
    	    this.secpages = new ArrayList<PgToken>();
			this.tokens = new ArrayList<PgToken>();

	        pstmt = StmtLibrary.getPSPNLFIELD(this.PNLNAME);
    	    rs = pstmt.executeQuery();

			// TODO: Throw exception if no records were read (need to use counter, no method on rs available).
	        while(rs.next()) {

				PgToken pf = new PgToken();
				pf.RECNAME = rs.getString("RECNAME").trim();
				pf.FIELDNAME = rs.getString("FIELDNAME").trim();
				pf.SUBPNLNAME = rs.getString("SUBPNLNAME").trim();
				pf.OCCURSLEVEL = rs.getInt("OCCURSLEVEL");
				pf.FIELDUSE = (byte) rs.getInt("FIELDUSE");

				/**
				 * Issue request for the record definition and record fields,
				 * regardless of field type.
		 		 */
				DefnCache.getRecord(pf.RECNAME);

    	        switch(rs.getInt("FIELDTYPE")) {

					case 1: 	// frame
						break;
					case 23: 	// horizontal rule
						break;
					case 3: 	// static image
						break;
					case 0:	 	// text (on page, linked to msg set/nbr, not in component or page buffer)
						break;
					case 2:
						pf.flags.add(AFlag.GROUPBOX);
						this.tokens.add(pf);
						break;
    	            case 11:
						pf.flags.add(AFlag.PAGE);
						pf.flags.add(AFlag.SUBPAGE);
						this.subpages.add(pf);
						this.tokens.add(pf);
                   		break;
	                case 18:
						pf.flags.add(AFlag.PAGE);
						pf.flags.add(AFlag.SECPAGE);
            	        this.secpages.add(pf);
						this.tokens.add(pf);
						break;

					case 10: // scroll bar
					case 19: // grid
        			case 27: // scroll area
						pf.flags.add(AFlag.SCROLL_START);
						this.tokens.add(pf);
						break;

		      		default:
						pf.flags.add(AFlag.GENERIC);
						this.tokens.add(pf);

						if(pf.RECNAME.length() == 0 || pf.FIELDNAME.length() == 0) {
							throw new EntVMachRuntimeException("A generic field with either a blank RECNAME " +
								"or FIELDNAME was encountered.");
						}
            	}
        	}
        } catch(java.sql.SQLException sqle) {
            log.fatal(sqle.getMessage(), sqle);
            System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
        } finally {
            try {
                if(rs != null) { rs.close(); }
                if(pstmt != null) { pstmt.close(); }
            } catch(java.sql.SQLException sqle) {}
        }
    }

	public void recursivelyLoadSubpages() {
		Page loadedPage = DefnCache.getPage(this.PNLNAME);
		for(PgToken tok : loadedPage.subpages) {
			Page p = DefnCache.getPage(tok.SUBPNLNAME);
			p.recursivelyLoadSubpages();
		}
	}

	/**
	 * This method is complex for a reason. PeopleTools recursively iterates through
	 * subpages before recursively iterating through secpages. However, during both sets
	 * of recursion, the order in which records are first referenced must be preserved in a buffer;
	 * when the secpage recursion begins, each occurrence of a secpage must cause the buffer
	 * to be flushed in the form of SQL requests for each record's Record PC listing. To accomplish this,
	 * we must be able to "expand" the secpages during secpage recursion, with emphasis on the fact that
	 * the expansions must be done in place, surrounded by all the other fields that come before/after it
	 * in the recursive traversal. The RecordPCListRequestBuffer abstracts the expansion process away
	 * from this routine.
	 */
	public void recursivelyLoadSecpages() {

		Page loadedPage = DefnCache.getPage(this.PNLNAME);
		Page p;

		ArrayList<PgToken> secpageMarkers = new ArrayList<PgToken>();

		// Recursively expand/search subpages for secpages.
		for(PgToken tok : loadedPage.tokens) {

			if(tok.flags.contains(AFlag.SUBPAGE)) {
				p = DefnCache.getPage(tok.SUBPNLNAME);
				p.recursivelyLoadSecpages();

			} else if(tok.flags.contains(AFlag.SECPAGE)) {

				/**
				 * Create a new PgToken instead of using the current one,
				 * otherwise there could be issues if the same token is submitted twice,
				 * which is valid because secpages can appear on multiple pages.
				 */
				PgToken marker = new PgToken(AFlag.SECPAGE);
				marker.SUBPNLNAME = tok.SUBPNLNAME;
				secpageMarkers.add(marker);
				RecordPCListRequestBuffer.queueSecpageToken(marker);

			} else if(tok.SUBPNLNAME.length() == 0 && tok.RECNAME.length() > 0 &&
					tok.FIELDNAME.length() > 0) {

				/**
				 * The RECNAME on this token will be used to query for the record's
				 * Record PC listing if it is the first instance of the RECNAME in
				 * the expanded stream.
				 */
				RecordPCListRequestBuffer.queueFieldToken(tok);
			}
		}

		// Then, recursively expand/search secpages for more secpages.
		for(PgToken marker : secpageMarkers) {
			RecordPCListRequestBuffer.notifyStartOfExpansion(marker);
			RecordPCListRequestBuffer.flushUpTo(marker);
			p = DefnCache.getPage(marker.SUBPNLNAME);
			p.recursivelyLoadSecpages();
			RecordPCListRequestBuffer.notifyEndOfExpansion(marker);
		}
	}

	public void discoverPagePC() {

		if(this.hasDiscoveredPagePC) { return; }
		this.hasDiscoveredPagePC = true;

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// Check to see if this page has any Page PeopleCode associated with it.
   	    	pstmt = StmtLibrary.getPSPCMPROG_RecordPCList(PSDefn.PAGE, this.PNLNAME);
	        rs = pstmt.executeQuery();
			while(rs.next()) {
				PeopleCodeProg prog = new PagePeopleCodeProg(this.PNLNAME);
				this.pageActivateProg = DefnCache.getProgram(prog);
			}
        } catch(java.sql.SQLException sqle) {
            log.fatal(sqle.getMessage(), sqle);
            System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
        } finally {
            try {
                if(rs != null) { rs.close(); }
                if(pstmt != null) { pstmt.close(); }
            } catch(java.sql.SQLException sqle) {}
        }
	}
}
