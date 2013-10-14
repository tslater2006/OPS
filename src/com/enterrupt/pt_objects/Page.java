package com.enterrupt.pt_objects;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import com.enterrupt.BuildAssistant;
import com.enterrupt.sql.StmtLibrary;

public class Page {

    public String PNLNAME;
    public ArrayList<String> subpages;
    public ArrayList<String> secpages;

    public Page(String pnlname) {
        this.PNLNAME = pnlname;
        this.subpages = new ArrayList<String>();
        this.secpages = new ArrayList<String>();
    }

    public void loadInitialMetadata() throws Exception {

        PreparedStatement pstmt;
        ResultSet rs;

        pstmt = StmtLibrary.getPSPNLDEFN(this.PNLNAME);
        rs = pstmt.executeQuery();
        rs.next(); // Do nothing with record for now.
        rs.close();
        pstmt.close();

        pstmt = StmtLibrary.getPSPNLFIELD(this.PNLNAME);
        rs = pstmt.executeQuery();

        ArrayList<String> subpanels = new ArrayList<String>();
        while(rs.next()) {
            switch(rs.getInt("FIELDTYPE")) {
                case 11:
                    this.subpages.add(rs.getString("SUBPNLNAME"));
                    break;
                case 18:
                    this.secpages.add(rs.getString("SUBPNLNAME"));
                    break;
                default:
                    BuildAssistant.addRecordField(rs.getString("RECNAME") + "." + rs.getString("FIELDNAME"));
            }
        }
        rs.close();
        pstmt.close();
    }
}
