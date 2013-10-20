package com.enterrupt.pt_objects;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import com.enterrupt.BuildAssistant;
import com.enterrupt.sql.StmtLibrary;

public class Record {

    public String RECNAME;
	public String RELLANGRECNAME;

    public Record(String recname) {
        this.RECNAME = recname;
    }

    public void loadInitialMetadata() throws Exception {

		// Do not contain if record definition has been cached.
		if(BuildAssistant.recDefnCache.get(this.RECNAME) != null) {
			return;
		}

		System.out.println("Loading Record " + this.RECNAME + "...");

		int fieldcount = 0;
		ArrayList<String> subrecnames = new ArrayList<String>();

        PreparedStatement pstmt;
        ResultSet rs;

        pstmt = StmtLibrary.getPSRECDEFN(this.RECNAME);
        rs = pstmt.executeQuery();

		if(rs.next()) {
			fieldcount = rs.getInt("FIELDCOUNT");
			this.RELLANGRECNAME = rs.getString("RELLANGRECNAME").trim();
		} else {
			System.out.println("[ERROR] Expected record to be returned from PSRECDEFN query.");
			System.exit(1);
		}
        rs.close();
        pstmt.close();

        pstmt = StmtLibrary.getPSDBFIELD_PSRECFIELD_JOIN(this.RECNAME);
        rs = pstmt.executeQuery();

		int i = 0;
        while(rs.next()) {
            i++;
        }
        rs.close();
        pstmt.close();

		/**
		 * If the number of fields retrieved differs from the FIELDCOUNT for the record
 		 * definition, we need to query for subrecords.
		 */
		if(fieldcount != i) {
			pstmt = StmtLibrary.getSubrecordsUsingPSDBFIELD_PSRECFIELD_JOIN(this.RECNAME);
			rs = pstmt.executeQuery();

			while(rs.next()) {
				subrecnames.add(rs.getString("FIELDNAME"));
				i++;
			}

			if(fieldcount != i) {
				System.out.println("[ERROR] Even after querying for subrecords, field count does not match that on PSRECDEFN.");
				System.exit(1);
			}
		}

        pstmt = StmtLibrary.getPSDBFLDLBL(this.RECNAME);
        rs = pstmt.executeQuery();
        while(rs.next()) {
            // Do nothing with records for now.
        }
        rs.close();
        pstmt.close();

		System.out.println("Loaded Record: " + this.RECNAME);

		// Cache the record definition.
		BuildAssistant.cacheRecord(this);

		for(String subrecname : subrecnames) {
			System.out.println("Loading subrecord : " + subrecname);
			Record r = new Record(subrecname);
			r.loadInitialMetadata();
		}

		/**
		 * If RELLANGRECNAME is not blank, load it here.
		 * If this part causes issues, consider:
		 *   1) moving this above the subrecord initialization.
		 * TODO: the record attached as RELLANGRECNAME may not have a discernible use or applicability
	     * to Enterrupt; need to look into this.
		 */
		if(this.RELLANGRECNAME.length() > 0) {
			Record r = new Record(this.RELLANGRECNAME);
			r.loadInitialMetadata();
		}
    }
}

