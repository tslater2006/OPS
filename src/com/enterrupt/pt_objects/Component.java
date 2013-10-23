package com.enterrupt.pt_objects;

import com.enterrupt.BuildAssistant;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Stack;
import java.sql.ResultSet;
import java.util.HashMap;
import java.sql.Blob;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.parser.Parser;
import com.enterrupt.interpreter.Interpreter;
import com.enterrupt.ComponentBuffer;

public class Component {

    public ArrayList<Page> pages;
    private String PNLGRPNAME;
    private String MARKET;

    private String SEARCHRECNAME; // name of search record for this component
    private ArrayList<PeopleCodeProg> searchRecordProgs;

    public Component(String pnlgrpname, String market) {
        this.PNLGRPNAME = pnlgrpname;
        this.MARKET = market;
        pages = new ArrayList<Page>();
        searchRecordProgs = new ArrayList<PeopleCodeProg>();
    }

    public void loadInitialMetadata() throws Exception {

        PreparedStatement pstmt;
        ResultSet rs;

        pstmt = StmtLibrary.getPSPNLGRPDEFN(this.PNLGRPNAME, this.MARKET);
        rs = pstmt.executeQuery();
        rs.next();
        this.SEARCHRECNAME = rs.getString("SEARCHRECNAME");
        rs.close();
        pstmt.close();

        pstmt = StmtLibrary.getPSPNLGROUP(this.PNLGRPNAME, this.MARKET);
        rs = pstmt.executeQuery();
        while(rs.next()) {
			// all pages at the root of the component start at scroll level 0.
			Page p = new Page(rs.getString("PNLNAME"));
            this.pages.add(p);
        }
        rs.close();
        pstmt.close();
    }

	public void loadSearchRecord() throws Exception {

		Record r = new Record(this.SEARCHRECNAME);
		r.loadInitialMetadata();
    }

    public void getListOfComponentPC() throws Exception {

        PreparedStatement pstmt;
        ResultSet rs;

        pstmt = StmtLibrary.getPSPCMPROG_CompPCList(PSDefn.COMPONENT, this.PNLGRPNAME,
                                                        PSDefn.MARKET, this.MARKET);
        rs = pstmt.executeQuery();
        while(rs.next()) {
            // Do nothing with these records for now.
        }
        rs.close();
        pstmt.close();
    }

    public void loadAndRunRecordPConSearchRecord() throws Exception {

        PreparedStatement pstmt;
        ResultSet rs;

        pstmt = StmtLibrary.getPSPCMPROG_SearchRecordPCList(PSDefn.RECORD, this.SEARCHRECNAME);
        rs = pstmt.executeQuery();
        while(rs.next()) {
			PeopleCodeProg prog = new PeopleCodeProg();
			prog.initRecordPCProg(rs.getString("OBJECTVALUE1"),
								  rs.getString("OBJECTVALUE2"),
								  rs.getString("OBJECTVALUE3"));
            this.searchRecordProgs.add(prog);
        }
        rs.close();
        pstmt.close();

        for(PeopleCodeProg prog : this.searchRecordProgs) {
            if(prog.event.equals("SearchInit")) {
				prog.loadInitialMetadata();
				Parser.parse(prog);
				Interpreter.run(prog);
            }
        }
    }

	/**
	 * IMPORTANT TODO: In all likelihood, PT is using the data gathered in a
     * previous query (the one above in getListOfComponentPC()) to run
     * Component PC attached to the search record. However, after running that query in
	 * SQL Developer, I can see that there are two SearchInit Component PC events, one for
     * LS_SS_PERS_SRCH and the other for STDNT_SRCH. In the trace file, only the event
     * attached to LS_SS_PERS_SRCH is run, likely because STDNT_SRCH is not the search record.
     * This logic needs to be implemented, but for now I'm simply describing it and will tie this
     * down later.
     */
	public void loadAndRunComponentPConSearchRecord() throws Exception {

		PeopleCodeProg prog = new PeopleCodeProg();
		prog.initComponentPCProg(this.PNLGRPNAME, this.MARKET, this.SEARCHRECNAME, "SearchInit");
		prog.loadInitialMetadata();
		Parser.parse(prog);
		Interpreter.run(prog);
	}

	/**
	 * Note: The search record fill query can't be executed as a 100% prepared stmt b/c PT
	 * hardcodes the EMPLID in the query for some reason. This statement will also need to be
	 * dynamically generated based on the fields present, etc., which I'm skipping for now in order
     * to move on and get a better understanding of the patterns of SQL present in the next part
	 * of the trace file.
	 * TODO: Address the notes presented above.
	 */
	public void fillSearchRecord() throws Exception {

		PreparedStatement pstmt;
		ResultSet rs;

		pstmt = StmtLibrary.getSearchRecordFillQuery();
		rs = pstmt.executeQuery();

		while(rs.next()) {
			// Do nothing with records for now.
		}
		rs.close();
		pstmt.close();
	}

	public void loadPages() throws Exception {
		for(Page p : this.pages) {
			p.recursivelyLoadSubpages();
		}
		for(Page p : this.pages) {
			p.recursivelyLoadSecpages();
		}
	}

	public void assembleComponentStructure() throws Exception {

		PgToken tok;
		PgTokenStream pfs;

		final byte REL_DISP_FLAG = (byte) 16;

		for(Page p : this.pages) {
			pfs = new PgTokenStream(p.PNLNAME);

			Stack<ScrollMarker> scrollMarkers = new Stack<ScrollMarker>();
			scrollMarkers.push(new ScrollMarker(0, null, AFlag.PAGE));

			while((tok = pfs.next()) != null) {

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

				// Remember: don't use continue here, flag can be attached to regular fields.
				if(tok.flags.contains(AFlag.SCROLL_LVL_DECREMENT)) {
					scrollMarkers.pop();
				}

				if(tok.RECNAME.length() > 0 &&
				   tok.FIELDNAME.length() > 0) {
					ComponentBuffer.addPageField(tok, scrollMarkers.peek().scrollLevel,
						scrollMarkers.peek().primaryRecName);
				}
				//System.out.println("[" + scrollMarkers.peek().scrollLevel + ", " +
				//	scrollMarkers.peek().primaryRecName + "]\t" + tok.flags);
			}

			System.out.println("Stack size: " + scrollMarkers.size());
		}
	}
}

class ScrollMarker {
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
