package com.enterrupt.pt_objects;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import com.enterrupt.sql.StmtLibrary;

public class RecordPeopleCodeProg extends PeopleCodeProg {

	public String RECNAME;
	public String FLDNAME;

	public RecordPeopleCodeProg(String recname, String fldname, String event) {
		super();
		this.RECNAME = recname;
		this.FLDNAME = fldname;
		this.event = event;
	}

	public void loadInitialMetadata() throws Exception {

		if(this.hasInitialMetadataBeenLoaded) {
            return;
        }

        PreparedStatement pstmt = null;
        ResultSet rs;

        // Get program text.
        pstmt = StmtLibrary.getPSPCMPROG_GetPROGTXT(PSDefn.RECORD, this.RECNAME,
                                                    PSDefn.FIELD, this.FLDNAME,
                                                    PSDefn.EVENT, this.event,
                                                    "0", PSDefn.NULL,
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
        pstmt = StmtLibrary.getPSPCMPROG_GetRefs(PSDefn.RECORD, this.RECNAME,
                                                 PSDefn.FIELD, this.FLDNAME,
                                                 PSDefn.EVENT, this.event,
                                                 "0", PSDefn.NULL,
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

        this.hasInitialMetadataBeenLoaded = true;
	}

	public Clob getProgTextClob() throws Exception {

        PreparedStatement pstmt = null;
        ResultSet rs;
        pstmt = StmtLibrary.getPSPCMTXT(PSDefn.RECORD, this.RECNAME,
                                        PSDefn.FIELD, this.FLDNAME,
                                        PSDefn.EVENT, this.event,
                                        "0", PSDefn.NULL,
                                        "0", PSDefn.NULL,
                                        "0", PSDefn.NULL,
                                        "0", PSDefn.NULL);
        rs = pstmt.executeQuery();
        if(rs.next()) {
            return rs.getClob("PCTEXT");
		}
		return null;
	}
}
