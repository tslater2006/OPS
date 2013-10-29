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

	public String getDescriptor() {
		return "ComponentPC." + this.PNLGRPNAME + "." + this.MARKET + "." + this.RECNAME + "." + this.event;
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

        /**
         * Append the program bytecode; there could be multiple records
         * for this program if the length exceeds 28,000 bytes. Note that
         * the above query must be ordered by PROSEQ, otherwise these records
         * will need to be pre-sorted before appending the BLOBs together.
         */
        int PROGLEN = -1;
        while(rs.next()) {
            PROGLEN = rs.getInt("PROGLEN");     // PROGLEN is the same for all records returned here.
            this.appendProgBytes(rs.getBlob("PROGTXT"));
        }
        rs.close();
        pstmt.close();

        if(this.progBytes.length != PROGLEN) {
            System.out.println("[ERROR] Number of bytes in " + this.getDescriptor() + " ("
                + this.progBytes.length + ") not equal to PROGLEN (" + PROGLEN + ").");
            System.exit(1);
        }

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
