package com.enterrupt.pt_objects;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.lang.StringBuilder;
import com.enterrupt.sql.StmtLibrary;

public class ComponentPeopleCodeProg extends ClassicPeopleCodeProg {

	public String PNLGRPNAME;
	public String MARKET;
	public String RECNAME;
	public String FLDNAME;
	private String[] bindVals;

	public ComponentPeopleCodeProg(String pnlgrpname, String market, String event) {
		super();
		this.PNLGRPNAME = pnlgrpname;
		this.MARKET = market;
		this.event = event;
		this.initBindVals();
	}

	public ComponentPeopleCodeProg(String pnlgrpname, String market, String recname, String event) {
		super();
		this.PNLGRPNAME = pnlgrpname;
		this.MARKET = market;
		this.RECNAME = recname;
		this.event = event;
		this.initBindVals();
	}

	public ComponentPeopleCodeProg(String pnlgrpname, String market, String recname, String fldname, String event) {
		super();
		this.PNLGRPNAME = pnlgrpname;
		this.MARKET = market;
		this.RECNAME = recname;
		this.FLDNAME = fldname;
		this.event = event;
		this.initBindVals();
	}

	private void initBindVals() {
		this.bindVals = new String[14];

		// Initialize empty defaults.
		this.bindVals[0] = PSDefn.COMPONENT;
		this.bindVals[1] = this.PNLGRPNAME;
		this.bindVals[2] = PSDefn.MARKET;
		this.bindVals[3] = this.MARKET;
		for(int i = 4; i < this.bindVals.length; i+=2) {
			this.bindVals[i] = "0";
			this.bindVals[i+1] = PSDefn.NULL;
		}

		int idx = 4;
		if(this.RECNAME != null) {
			this.bindVals[idx++] = PSDefn.RECORD;
			this.bindVals[idx++] = this.RECNAME;

			if(this.FLDNAME != null) {
				this.bindVals[idx++] = PSDefn.FIELD;
				this.bindVals[idx++] = this.FLDNAME;
			}
		}

		this.bindVals[idx++] = PSDefn.EVENT;
		this.bindVals[idx++] = this.event;
	}

	public String getDescriptor() {

		StringBuilder builder = new StringBuilder();
		builder.append("ComponentPC.").append(this.PNLGRPNAME).append(".").append(this.MARKET).append(".");

		if(this.RECNAME != null) {
			builder.append(this.RECNAME).append(".");

			if(this.FLDNAME != null) {
				builder.append(this.FLDNAME).append(".");
			}
		}

		builder.append(this.event);
		return builder.toString();
	}

 	protected void progSpecific_loadInitialMetadata() throws Exception {

        PreparedStatement pstmt = null;
        ResultSet rs;

        // Get program text.
        pstmt = StmtLibrary.getPSPCMPROG_GetPROGTXT(this.bindVals[0], this.bindVals[1],
													this.bindVals[2], this.bindVals[3],
													this.bindVals[4], this.bindVals[5],
													this.bindVals[6], this.bindVals[7],
													this.bindVals[8], this.bindVals[9],
													this.bindVals[10], this.bindVals[11],
													this.bindVals[12], this.bindVals[13]);
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
        pstmt = StmtLibrary.getPSPCMPROG_GetRefs(this.bindVals[0], this.bindVals[1],
													this.bindVals[2], this.bindVals[3],
													this.bindVals[4], this.bindVals[5],
													this.bindVals[6], this.bindVals[7],
													this.bindVals[8], this.bindVals[9],
													this.bindVals[10], this.bindVals[11],
													this.bindVals[12], this.bindVals[13]);
        rs = pstmt.executeQuery();
        while(rs.next()) {
            this.progRefsTable.put(rs.getInt("NAMENUM"),
                new Reference(rs.getString("RECNAME").trim(), rs.getString("REFNAME").trim()));

			//System.out.println(rs.getString("RECNAME") + " " + rs.getString("REFNAME"));
        }
        rs.close();
        pstmt.close();
    }

	public Clob getProgTextClob() throws Exception {

        PreparedStatement pstmt = null;
        ResultSet rs;
        pstmt = StmtLibrary.getPSPCMTXT(this.bindVals[0], this.bindVals[1],
													this.bindVals[2], this.bindVals[3],
													this.bindVals[4], this.bindVals[5],
													this.bindVals[6], this.bindVals[7],
													this.bindVals[8], this.bindVals[9],
													this.bindVals[10], this.bindVals[11],
													this.bindVals[12], this.bindVals[13]);
        rs = pstmt.executeQuery();
        if(rs.next()) {
            return rs.getClob("PCTXT");
		}
		return null;
	}
}
