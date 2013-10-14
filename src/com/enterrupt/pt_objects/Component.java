package com.enterrupt.pt_objects;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.HashMap;
import java.sql.Blob;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.interpreter.PCInterpreter;

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

    public void loadAndRunSearchRecordPC() throws Exception {

        PreparedStatement pstmt;
        ResultSet rs;

        pstmt = StmtLibrary.getPSPCMPROG_SearchRecordPCList(PSDefn.RECORD, this.SEARCHRECNAME);
        rs = pstmt.executeQuery();
        while(rs.next()) {
            this.searchRecordProgs.add(new PeopleCodeProg(rs.getString("OBJECTVALUE1"),
                                                          rs.getString("OBJECTVALUE2"),
                                                          rs.getString("OBJECTVALUE3")));
        }
        rs.close();
        pstmt.close();

        for(PeopleCodeProg prog : this.searchRecordProgs) {
            if(prog.event.equals("SearchInit")) {

                // Get program text.
                pstmt = StmtLibrary.getPSPCMPROG_GetPROGTXT(PSDefn.RECORD, prog.recname,
                                                            PSDefn.FIELD, prog.fldname,
                                                            PSDefn.EVENT, prog.event,
                                                            "0", PSDefn.NULL,
                                                            "0", PSDefn.NULL,
                                                            "0", PSDefn.NULL,
                                                            "0", PSDefn.NULL);
                rs = pstmt.executeQuery();
                if(rs.next()) {
                    prog.setProgText(rs.getBlob("PROGTXT"));
                }
                rs.close();
                pstmt.close();
                // Get program references.
                pstmt = StmtLibrary.getPSPCMPROG_GetRefs(PSDefn.RECORD, prog.recname,
                                                         PSDefn.FIELD, prog.fldname,
                                                         PSDefn.EVENT, prog.event,
                                                         "0", PSDefn.NULL,
                                                         "0", PSDefn.NULL,
                                                         "0", PSDefn.NULL,
                                                         "0", PSDefn.NULL);
                rs = pstmt.executeQuery();
                while(rs.next()) {
                    // Do nothing with records for now.
                }
                rs.close();
                pstmt.close();

                PCInterpreter.interpret(prog);
            }
        }
    }
}
