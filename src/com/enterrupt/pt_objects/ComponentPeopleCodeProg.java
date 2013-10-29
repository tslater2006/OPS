package com.enterrupt.pt_objects;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import com.enterrupt.sql.StmtLibrary;

public class ComponentPeopleCodeProg extends PeopleCodeProg {

	public String PNLGRPNAME;
	public String MARKET;
	public String RECNAME;

	public ComponentPeopleCodeProg(String pnlgrpname, String market, String recname, String event) {
		super();
		this.PNLGRPNAME = pnlgrpname;
		this.MARKET = market;
		this.RECNAME = recname;
		this.event = event;
	}

 	protected void progSpecific_loadInitialMetadata() throws Exception {

        PreparedStatement pstmt = null;
        ResultSet rs;

        // Get program text.
        pstmt = StmtLibrary.getPSPCMPROG_GetPROGTXT(PSDefn.COMPONENT, this.PNLGRPNAME,
                                                    PSDefn.MARKET, this.MARKET,
                                                    PSDefn.RECORD, this.RECNAME,
                                                    PSDefn.EVENT, this.event,
                                                    "0", PSDefn.NULL,
                                                    "0", PSDefn.NULL,
                                                    "0", PSDefn.NULL);
        rs = pstmt.executeQuery();
        if(rs.next()) {
            this.setProgText(rs.getBlob("PROGTXT"));
        }
        rs.close();
        pstmt.close();

    	// Get program references.
        pstmt = StmtLibrary.getPSPCMPROG_GetRefs(PSDefn.COMPONENT, this.PNLGRPNAME,
                                                 PSDefn.MARKET, this.MARKET,
                                                 PSDefn.RECORD, this.RECNAME,
                                                 PSDefn.EVENT, this.event,
                                                 "0", PSDefn.NULL,
                                                 "0", PSDefn.NULL,
                                                 "0", PSDefn.NULL);
        rs = pstmt.executeQuery();
        while(rs.next()) {
            this.progRefsTable.put(rs.getInt("NAMENUM"),
                new Reference(rs.getString("RECNAME").trim(), rs.getString("REFNAME").trim()));
        }
        rs.close();
        pstmt.close();
    }

	public Clob getProgTextClob() throws Exception {

        PreparedStatement pstmt = null;
        ResultSet rs;
        pstmt = StmtLibrary.getPSPCMTXT(PSDefn.COMPONENT, this.PNLGRPNAME,
                                        PSDefn.MARKET, this.MARKET,
                                        PSDefn.RECORD, this.RECNAME,
                                        PSDefn.EVENT, this.event,
                                        "0", PSDefn.NULL,
                                        "0", PSDefn.NULL,
                                        "0", PSDefn.NULL);
        rs = pstmt.executeQuery();
        if(rs.next()) {
            return rs.getClob("PCTXT");
		}
		return null;
	}
}
