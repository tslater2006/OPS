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

    public void loadInitialMetadata(int indent) throws Exception {

		for(int i=0; i<indent; i++){ System.out.print(" "); }
		System.out.println("Page: " + this.PNLNAME);

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

			/**
			 * If the RECNAME field is not blank, load it's record metadata, regardless of
			 * what type of field the record represents.
			 */
			String recname = rs.getString("RECNAME").trim();
			if(BuildAssistant.recDefnCache.get(recname) == null && recname.trim().length() > 0) {

				for(int i=0; i<indent + 1; i++){ System.out.print(" "); }
				System.out.println("Record: " + recname + " (" + rs.getString("FIELDTYPE") + ")");

				Record r = new Record(recname);
				r.loadInitialMetadata();
			}
        }
        rs.close();
        pstmt.close();
    }
}

