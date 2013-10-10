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
	private ArrayList<PeopleCodeProg> searchRecordProgs;

	public Component(String pnlgrpname, String market) {
		this.PNLGRPNAME = pnlgrpname;
		this.MARKET = market;
		pages = new ArrayList<String>();
		searchRecordProgs = new ArrayList<PeopleCodeProg>();
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

	public void getListOfComponentPC() {

		PreparedStatement pstmt;
        ResultSet rs;

        try {
            pstmt = StmtLibrary.getPSPCMPROG_CompPCList(PSDefn.COMPONENT, this.PNLGRPNAME,
														PSDefn.MARKET, this.MARKET);
            rs = pstmt.executeQuery();
            while(rs.next()) {
				// Do nothing with these records for now.
            }
            rs.close();
            pstmt.close();

        } catch(Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
	}

	public void loadAndRunSearchRecordPC() {

		PreparedStatement pstmt;
        ResultSet rs;

        try {
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
						System.out.println(rs.getBlob("PROGTXT").length());
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

					/**
				     * TODO: Call PC interpreter.
				     */
				}
			}

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

class PeopleCodeProg {

	public String recname;
	public String fldname;
	public String event;

	public PeopleCodeProg(String recname, String fldname, String event) {
		this.recname = recname;
		this.fldname = fldname;
		this.event = event;
	}
}
