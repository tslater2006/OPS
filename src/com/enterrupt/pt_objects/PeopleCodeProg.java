package com.enterrupt.pt_objects;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Blob;
import java.lang.StringBuilder;
import java.util.HashMap;
import com.enterrupt.sql.StmtLibrary;

public class PeopleCodeProg {

	public String pcType;
	public String pnlgrpname;
	public String market;
    public String recname;
    public String fldname;
    public String event;
	private StringBuilder progTextBuilder;
    public byte[] progBytes;
	public HashMap<Integer, String> progRefsTable;

	public int byteCursorPos = 0;

    public PeopleCodeProg() {
		this.progRefsTable = new HashMap<Integer, String>();
    }

	public void initRecordPCProg(String recname, String fldname, String event) {
		this.pcType = "RecordPC";
		this.recname = recname;
        this.fldname = fldname;
        this.event = event;
	}

	public void initComponentPCProg(String pnlgrpname, String market, String recname, String event) {
		this.pcType = "ComponentPC";
		this.pnlgrpname = pnlgrpname;
		this.market = market;
		this.recname = recname;
		this.event = event;
	}

    public void setProgText(Blob b) throws Exception {

        /**
         * TODO: Here we risk losing progtext bytes since the length (originally a long)
         * is cast to int. Build in a check for this, use binarystream when this is the case.
         */
        int num_bytes = (int) b.length();
        this.progBytes = b.getBytes(1, num_bytes); // first byte is at idx 1
    }

	public void setByteCursorPos(int pos) {
		this.byteCursorPos = pos;
	}

	public byte getCurrentByte() {
		return this.progBytes[this.byteCursorPos];
	}

	public byte readNextByte() {
		System.out.printf("[READ] %d: 0x%02X\n", this.byteCursorPos, this.progBytes[this.byteCursorPos]);
		return this.progBytes[this.byteCursorPos++];
	}

	public byte readAhead() {
		if(this.byteCursorPos >= this.progBytes.length - 1) {
			return -1;
		}
		return this.progBytes[this.byteCursorPos];
	}

	public void appendProgText(char c) {
		if(this.progTextBuilder == null) {
			this.progTextBuilder = new StringBuilder();
		}
		progTextBuilder.append(c);
	}

	public void appendProgText(String s) {
		if(this.progTextBuilder == null) {
			this.progTextBuilder = new StringBuilder();
		}
		progTextBuilder.append(s);
	}

	public String getProgText() {
		return this.progTextBuilder.toString();
	}

	public String getProgReference(int refNbr) {
		return this.progRefsTable.get(refNbr);
	}

	public void loadInitialMetadata() throws Exception {

		PreparedStatement pstmt = null;
		ResultSet rs;

		// Get program text.
        if(this.pcType.equals("RecordPC")) {
			pstmt = StmtLibrary.getPSPCMPROG_GetPROGTXT(PSDefn.RECORD, this.recname,
                                                    PSDefn.FIELD, this.fldname,
                                                    PSDefn.EVENT, this.event,
                                                    "0", PSDefn.NULL,
                                                    "0", PSDefn.NULL,
                                                    "0", PSDefn.NULL,
                                                    "0", PSDefn.NULL);
		} else if(this.pcType.equals("ComponentPC")) {
			pstmt = StmtLibrary.getPSPCMPROG_GetPROGTXT(PSDefn.COMPONENT, this.pnlgrpname,
                                                    PSDefn.MARKET, this.market,
													PSDefn.RECORD, this.recname,
                                                    PSDefn.EVENT, this.event,
                                                    "0", PSDefn.NULL,
                                                    "0", PSDefn.NULL,
                                                    "0", PSDefn.NULL);
		} else {
			System.out.println("[ERROR]: Unexpected value for pcType.");
			System.exit(1);
		}

       	rs = pstmt.executeQuery();
        if(rs.next()) {
        	this.setProgText(rs.getBlob("PROGTXT"));
        }
        rs.close();
        pstmt.close();

		// Index the reference keywords.
		/**
		 * TODO: Put this in a place where it is only done once; doing this for every
		 * program is a waste.
		 */
		HashMap<String, String> refReservedWordsTable = new HashMap<String, String>();
		for(String s : PSDefn.refReservedWords) {
			refReservedWordsTable.put(s.toUpperCase(), s);
		}

		// Get program references.
        if(pcType.equals("RecordPC")) {
			pstmt = StmtLibrary.getPSPCMPROG_GetRefs(PSDefn.RECORD, this.recname,
                                                 PSDefn.FIELD, this.fldname,
                                                 PSDefn.EVENT, this.event,
                                                 "0", PSDefn.NULL,
                                                 "0", PSDefn.NULL,
                                                 "0", PSDefn.NULL,
                                                 "0", PSDefn.NULL);
        } else if(pcType.equals("ComponentPC")) {
			pstmt = StmtLibrary.getPSPCMPROG_GetRefs(PSDefn.COMPONENT, this.pnlgrpname,
												 PSDefn.MARKET, this.market,
                                                 PSDefn.RECORD, this.recname,
                                                 PSDefn.EVENT, this.event,
                                                 "0", PSDefn.NULL,
                                                 "0", PSDefn.NULL,
                                                 "0", PSDefn.NULL);
		} else {
			System.out.println("[ERROR] Unexpected value for pcType.");
			System.exit(1);
		}

		rs = pstmt.executeQuery();
        while(rs.next()) {
        	Integer NAMENUM = rs.getInt("NAMENUM");
			if(progRefsTable.get(NAMENUM) != null) {
				/**
				 * TODO: Make this merely a warning and don't stop execution. A duplicate ref
				 * shouldn't matter anyway, but I want to exit(1) on it for now just to see how often
			     * it occurs.
				 */
				System.out.println("Warning: duplicate reference.");
				System.exit(1);
			}
			String RECNAME = rs.getString("RECNAME").trim();
			String reservedWord = refReservedWordsTable.get(RECNAME);
			if(reservedWord != null) {
				RECNAME = reservedWord;	// remember: this assigns the value (lowercase) rather than the key (upper).
			}
			String REFNAME = rs.getString("REFNAME").trim();
			this.progRefsTable.put(NAMENUM,
				(RECNAME != null && RECNAME.length() > 0 ? RECNAME + "." : "") + REFNAME);
        }
        rs.close();
        pstmt.close();
	}
}

