package com.enterrupt;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.HashMap;

class Menu {

	public String MENUNAME;

	public Menu(String menuname) {
		this.MENUNAME = menuname;
	}

	public void loadInitialMetadata() {

		PreparedStatement pstmt;
        ResultSet rs;

        try {
            pstmt = StmtLibrary.getPSMENUDEFN(this.MENUNAME);
            rs = pstmt.executeQuery();
            rs.next();      //Do nothing with record for now.
            rs.close();
            pstmt.close();

            pstmt = StmtLibrary.getPSMENUITEM(this.MENUNAME);
            rs = pstmt.executeQuery();
            while(rs.next()) {
				//Do nothing with records for now.
            }
            rs.close();
            pstmt.close();

        } catch(Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
	}
}

class Component {

	public ArrayList<String> pages;
	private String PNLGRPNAME;
	private String MARKET;

	private String SEARCHRECNAME; // name of search record for this component 

	public Component(String pnlgrpname, String market) {
		this.PNLGRPNAME = pnlgrpname;
		this.MARKET = market;
		pages = new ArrayList<String>();
	}

	public void loadInitialMetadata() {

		PreparedStatement pstmt;
        ResultSet rs;

        try {
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

        } catch(Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
	}

	public void loadSearchRecord() {

		PreparedStatement pstmt;
        ResultSet rs;

        try {
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

        } catch(Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
	}
}

class Page {

	public String PNLNAME;
	public ArrayList<String> subpages;
	public ArrayList<String> secpages;

	public Page(String pnlname) {
		this.PNLNAME = pnlname;
		this.subpages = new ArrayList<String>();
		this.secpages = new ArrayList<String>();
	}

	public void loadInitialMetadata() {

		PreparedStatement pstmt;
        ResultSet rs;

        try{

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

        } catch(Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
	}
}
