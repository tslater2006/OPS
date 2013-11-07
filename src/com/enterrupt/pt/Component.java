package com.enterrupt.pt;

import java.sql.*;
import java.util.*;
import java.io.*;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.parser.Parser;
import com.enterrupt.interpreter.Interpreter;
import com.enterrupt.buffers.*;
import com.enterrupt.runtime.*;
import com.enterrupt.pt.peoplecode.*;
import com.enterrupt.pt.pages.*;
import org.apache.logging.log4j.*;

public class Component {

    public String PNLGRPNAME;
    public String MARKET;
    public String SEARCHRECNAME; // name of search record for this component

    public ArrayList<Page> pages;
	public ArrayList<ComponentPeopleCodeProg> orderedComponentProgs;

	private static Logger log = LogManager.getLogger(Component.class.getName());

	private boolean hasListOfComponentPCBeenRetrieved = false;

	private class ScrollMarker {
		public String primaryRecName;
		public int scrollLevel;
		public AFlag src;

		public ScrollMarker() {}

		public ScrollMarker(int s, String p, AFlag a) {
			this.scrollLevel = s;
			this.primaryRecName = p;
			this.src = a;
		}
	}

    public Component(String pnlgrpname, String market) {

        this.PNLGRPNAME = pnlgrpname;
        this.MARKET = market;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

		try {
	        pstmt = StmtLibrary.getPSPNLGRPDEFN(this.PNLGRPNAME, this.MARKET);
    	    rs = pstmt.executeQuery();
       		rs.next();
			log.debug("Component search record: {}", rs.getString("SEARCHRECNAME"));
        	this.SEARCHRECNAME = rs.getString("SEARCHRECNAME");
        	rs.close();
        	pstmt.close();

        	this.pages = new ArrayList<Page>();

        	pstmt = StmtLibrary.getPSPNLGROUP(this.PNLGRPNAME, this.MARKET);
        	rs = pstmt.executeQuery();
        	while(rs.next()) {
				// All pages at the root of the component start at scroll level 0.
				Page p = new Page(rs.getString("PNLNAME"));
				log.debug("Component contains Page.{}", p.PNLNAME);
            	this.pages.add(p);
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

	public void loadSearchRecord() {
		// Loads the search record and puts it in cache.
		DefnCache.getRecord(this.SEARCHRECNAME);
    }

    public void getListOfComponentPC() {

		if(this.hasListOfComponentPCBeenRetrieved) { return; }
		this.hasListOfComponentPCBeenRetrieved = true;

        PreparedStatement pstmt = null;
  		ResultSet rs = null;

		try {
			this.orderedComponentProgs = new ArrayList<ComponentPeopleCodeProg>();

    	    pstmt = StmtLibrary.getPSPCMPROG_CompPCList(PSDefn.COMPONENT, this.PNLGRPNAME,
                                                        PSDefn.MARKET, this.MARKET);
       		rs = pstmt.executeQuery();

  	    	while(rs.next()) {

				String objectid3 = rs.getString("OBJECTID3").trim();
				String objectval3 = rs.getString("OBJECTVALUE3").trim();
				String objectid4 = rs.getString("OBJECTID4").trim();
				String objectval4 = rs.getString("OBJECTVALUE4").trim();
				String objectid5 = rs.getString("OBJECTID5").trim();
				String objectval5 = rs.getString("OBJECTVALUE5").trim();

				PeopleCodeProg prog = null;

				// Example: SSS_STUDENT_CENTER.GBL.PreBuild
				if(objectid3.equals(PSDefn.EVENT)) {
					prog = new ComponentPeopleCodeProg(this.PNLGRPNAME, this.MARKET,
						objectval3);

				// Example: SSS_STUDENT_CENTER.LS_SS_PERS_SRCH.SearchInit
				} else if(objectid3.equals(PSDefn.RECORD) && objectid4.equals(PSDefn.EVENT)) {
					prog = new ComponentPeopleCodeProg(this.PNLGRPNAME, this.MARKET,
						objectval3, objectval4);

				// Example: SSS_STUDENT_CENTER.LS_DERIVED_SSS_SCL.SS_CLS_SCHED_LINK.FieldChange
				} else if(objectid3.equals(PSDefn.RECORD) && objectid4.equals(PSDefn.FIELD) &&
							objectid5.equals(PSDefn.EVENT)) {
					prog = new ComponentPeopleCodeProg(this.PNLGRPNAME, this.MARKET,
						objectval3, objectval4, objectval5);

				} else {
					throw new EntVMachRuntimeException("Unexpected type of Component PC encountered.");
				}

				prog = DefnCache.getProgram(prog);
				this.orderedComponentProgs.add((ComponentPeopleCodeProg)prog);
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

    public void loadAndRunRecordPConSearchRecord() {

		Record recDefn = DefnCache.getRecord(this.SEARCHRECNAME);

		/**
		 * Record PC should be loaded at this time only if the search record
	 	 * contains one or more keys.
		 */
		if(!recDefn.hasAnyKeys()) {
			log.debug("No keys on search record.");
			return;
		}

		recDefn.discoverRecordPC();
   	    for(PeopleCodeProg prog : recDefn.orderedRecordProgs) {
       	    if(prog.event.equals("SearchInit")) {
				PeopleCodeProg p = DefnCache.getProgram(prog);
				p.init();
				Interpreter interpreter = new Interpreter(p);
				interpreter.run();
       	    }
       	}
    }

	public void loadAndRunComponentPConSearchRecord() {

		for(ComponentPeopleCodeProg prog : this.orderedComponentProgs) {
			if(prog.RECNAME != null && prog.RECNAME.equals(this.SEARCHRECNAME)) {
				PeopleCodeProg p = DefnCache.getProgram(prog);
				p.init();
				p.loadDefnsAndPrograms();
				Interpreter interpreter = new Interpreter(p);
				interpreter.run();
			}
		}
	}

	/**
	 * Note: The search record fill query can't be executed as a 100% prepared stmt b/c PT
	 * hardcodes the EMPLID in the query for some reason. This statement will also need to be
	 * dynamically generated based on the fields present, etc., which I'm skipping for now in order
     * to move on and get a better understanding of the patterns of SQL present in the next part
	 * of the trace file.
	 * TODO: Address the notes presented above.
	 */
	public void fillSearchRecord() {

		Record recDefn = DefnCache.getRecord(this.SEARCHRECNAME);

		/**
		 * Only fill the search record at this time if the record
		 * contains at least one key.
		 */
		if(!recDefn.hasAnyKeys()) {
			return;
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = StmtLibrary.getSearchRecordFillQuery();
			rs = pstmt.executeQuery();
			rs.next();   // Do nothing with records for now.

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

	public void loadPages() {
		for(Page p : this.pages) {
			p.recursivelyLoadSubpages();
		}

		RecordPCListRequestBuffer.init();
		for(Page p : this.pages) {
			p.recursivelyLoadSecpages();
		}

		RecordPCListRequestBuffer.flushEntireTokenStream();
	}

	public void assembleComponentStructure() {

		PgToken tok;
		PgTokenStream pfs;

		final byte REL_DISP_FLAG = (byte) 16;

		for(Page p : this.pages) {
			pfs = new PgTokenStream(p.PNLNAME);

			Stack<ScrollMarker> scrollMarkers = new Stack<ScrollMarker>();
			scrollMarkers.push(new ScrollMarker(0, null, AFlag.PAGE));

			while((tok = pfs.next()) != null) {

				//log.debug(tok);

				if(tok.flags.contains(AFlag.PAGE)) {
					ScrollMarker sm = new ScrollMarker();
					sm.src = AFlag.PAGE;
					sm.primaryRecName = scrollMarkers.peek().primaryRecName;
					sm.scrollLevel = scrollMarkers.peek().scrollLevel;
					scrollMarkers.push(sm);
					continue;
				}

				if(tok.flags.contains(AFlag.END_OF_PAGE)) {
					while(scrollMarkers.peek().src == AFlag.SCROLL_START) {
						scrollMarkers.pop(); // pop interim scroll levels.
					}
					scrollMarkers.pop();		// pop the matching page.
					continue;
				}

				if(tok.flags.contains(AFlag.SCROLL_START)) {

					// This scroll may appear right after an unended scroll; if so, pop the previous one.
					ScrollMarker topSm = scrollMarkers.peek();
					if(topSm.src == AFlag.SCROLL_START &&
						!tok.primaryRecName.equals(topSm.primaryRecName)) {
						scrollMarkers.pop();
					}

					ScrollMarker sm = new ScrollMarker();
					sm.src = AFlag.SCROLL_START;
					sm.primaryRecName = tok.primaryRecName;
					sm.scrollLevel = scrollMarkers.peek().scrollLevel + tok.OCCURSLEVEL;
					scrollMarkers.push(sm);
					continue;
				}

				// Remember: don't "continue" here, since SCROLL_LVL_DECREMENT can be attached to regular fields.
				if(tok.flags.contains(AFlag.SCROLL_LVL_DECREMENT)) {
					scrollMarkers.pop();
				}

				if(tok.doesBelongInComponentStructure()) {
					ComponentBuffer.addPageField(tok, scrollMarkers.peek().scrollLevel,
						scrollMarkers.peek().primaryRecName);
				}
			}

			if(scrollMarkers.size() != 0) {
				throw new EntVMachRuntimeException("Scroll marker stack size exceeds 0 " +
					"at the end of the page token stream.");
			}
		}
	}

	public void loadAllRecordPCProgsAndReferencedDefns() {

		IStreamableBuffer buf;

		ComponentBuffer.resetCursors();
		while((buf = ComponentBuffer.next()) != null) {

			if(buf instanceof RecordFieldBuffer) {
				RecordFieldBuffer fbuf = (RecordFieldBuffer) buf;

				ArrayList<PeopleCodeProg> fieldProgs = fbuf.recDefn.getRecordProgsForField(fbuf.fldName);
				if(fieldProgs != null) {

					for(PeopleCodeProg prog : fieldProgs) {
						PeopleCodeProg p = DefnCache.getProgram(prog);
						p.init();
						p.loadDefnsAndPrograms();
					}
				}
			}
		}
	}

	public void loadAllComponentPCProgsAndReferencedDefns() {

		// Load the PostBuild event for the component first, if it exists.
		for(ComponentPeopleCodeProg prog : this.orderedComponentProgs) {
			if(prog.RECNAME == null && prog.FLDNAME == null && prog.event.equals("PostBuild")) {
				PeopleCodeProg p = DefnCache.getProgram(prog);
				p.init();
				p.loadDefnsAndPrograms();
			}
		}

		// Then the PreBuild event, if it exists.
		for(ComponentPeopleCodeProg prog : this.orderedComponentProgs) {
			if(prog.RECNAME == null && prog.FLDNAME == null && prog.event.equals("PreBuild")) {
				PeopleCodeProg p = DefnCache.getProgram(prog);
				p.init();
				p.loadDefnsAndPrograms();
			}
		}

		// Then load each Component PC program in order of appearance in result set.
		for(ComponentPeopleCodeProg prog : this.orderedComponentProgs) {
			PeopleCodeProg p = DefnCache.getProgram(prog);
			p.init();
			p.loadDefnsAndPrograms();
		}
	}

	public void loadAllPagePC() {

		for(Page p : this.pages) {
			Page cachedPage = DefnCache.getPage(p.PNLNAME);
			cachedPage.discoverPagePC();
			if(cachedPage.pageActivateProg != null) {
				PeopleCodeProg pr = DefnCache.getProgram(cachedPage.pageActivateProg);
				pr.init();
				pr.loadDefnsAndPrograms();
			}
		}
	}
}
