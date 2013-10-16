package com.enterrupt.pt_objects;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.HashMap;
import java.sql.Blob;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.Parser;

public class Component {

    public ArrayList<String> pages;
    private String PNLGRPNAME;
    private String MARKET;

    private String SEARCHRECNAME; // name of search record for this component
    private ArrayList<PeopleCodeProg> searchRecordProgs;

    public Component(String pnlgrpname, String market) {
        this.PNLGRPNAME = pnlgrpname;
        this.MARKET = market;
        pages = new ArrayList<String>();
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
            this.pages.add(rs.getString("PNLNAME"));
        }
        rs.close();
        pstmt.close();
    }

	public void loadSearchRecord() throws Exception {

        PreparedStatement pstmt;
        ResultSet rs;

        pstmt = StmtLibrary.getPSRECDEFN(this.SEARCHRECNAME);
        rs = pstmt.executeQuery();
        rs.next();
        rs.close();
        pstmt.close();

        pstmt = StmtLibrary.getPSDBFIELD_PSRECFIELD_JOIN(this.SEARCHRECNAME);
        rs = pstmt.executeQuery();
        while(rs.next()) {
            // Do nothing with records for now.
        }
        rs.close();
        pstmt.close();

        pstmt = StmtLibrary.getPSDBFLDLBL(this.SEARCHRECNAME);
        rs = pstmt.executeQuery();
        while(rs.next()) {
            // Do nothing with records for now.
        }
        rs.close();
        pstmt.close();
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
				Parser.interpret(prog);
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
		Parser.interpret(prog);
	}
}
