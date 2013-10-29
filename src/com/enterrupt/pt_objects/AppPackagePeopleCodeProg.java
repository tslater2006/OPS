package com.enterrupt.pt_objects;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import com.enterrupt.BuildAssistant;
import com.enterrupt.sql.StmtLibrary;

public class AppPackagePeopleCodeProg extends PeopleCodeProg {

	public String[] bindValues;

	public AppPackagePeopleCodeProg(String[] path) {
		super();
		this.event = "OnExecute";

		/**
	     * Due to the variable length nature of app class paths,
		 * we need to determine which bind values we'll be querying the database
		 * with based on the length provided and knowledge of OBJECTID constants.
		 */
		int pathPartIdx = 0;
		int nextObjectId = 105; // 105 through 107 == App Class
		this.bindValues = new String[14];
		for(int i = 0; i < this.bindValues.length; i+=2) {
			if(i == 0) {
				this.bindValues[0] = "104"; // 104 == App Package
				this.bindValues[1] = path[pathPartIdx++];
			} else if(pathPartIdx == (path.length - 1)) {
				this.bindValues[i] = "107"; // 107 == final App Class in hierarchy
				this.bindValues[i+1] = path[pathPartIdx++];
			} else if(pathPartIdx < path.length) {
				this.bindValues[i] = "" + nextObjectId++;
				this.bindValues[i+1] = path[pathPartIdx++];
			} else {

				// The event OBJECTID/OBJECTVAL pair must follow the final app class.
				if(this.bindValues[i-2].equals("107")) {
					this.bindValues[i] = "12";		// 12 == Event
					this.bindValues[i+1] = this.event;
				} else {
					this.bindValues[i] = "0";
					this.bindValues[i+1] = PSDefn.NULL;
				}
			}
		}

		//System.out.println(java.util.Arrays.toString(this.bindValues));
		//System.exit(1);
	}

	private void getListOfAllConstituentAppClassPrograms() throws Exception {

		if(BuildAssistant.appPackageDefnCache.get(this.bindValues[1]) != null) {
			return;
		}

		PreparedStatement pstmt;
        ResultSet rs;

        pstmt = StmtLibrary.getPSPCMPROG_RecordPCList(this.bindValues[0], this.bindValues[1]);
        rs = pstmt.executeQuery();
		rs.next(); // Do nothing with records for now.
        rs.close();
        pstmt.close();

		BuildAssistant.cacheAppPackageDefn(this.bindValues[1]);
	}

	protected void progSpecific_loadInitialMetadata() throws Exception {

		this.getListOfAllConstituentAppClassPrograms();

        PreparedStatement pstmt = null;
        ResultSet rs;

        // Get program text.
        pstmt = StmtLibrary.getPSPCMPROG_GetPROGTXT(this.bindValues[0], this.bindValues[1],
													this.bindValues[2], this.bindValues[3],
													this.bindValues[4], this.bindValues[5],
													this.bindValues[6], this.bindValues[7],
													this.bindValues[8], this.bindValues[9],
													this.bindValues[10], this.bindValues[11],
													this.bindValues[12], this.bindValues[13]);
        rs = pstmt.executeQuery();
        if(rs.next()) {
            this.setProgText(rs.getBlob("PROGTXT"));
        }
        rs.close();
        pstmt.close();

    	// Get program references.
        pstmt = StmtLibrary.getPSPCMPROG_GetRefs(this.bindValues[0], this.bindValues[1],
													this.bindValues[2], this.bindValues[3],
													this.bindValues[4], this.bindValues[5],
													this.bindValues[6], this.bindValues[7],
													this.bindValues[8], this.bindValues[9],
													this.bindValues[10], this.bindValues[11],
													this.bindValues[12], this.bindValues[13]);
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

        pstmt = StmtLibrary.getPSPCMTXT(this.bindValues[0], this.bindValues[1],
													this.bindValues[2], this.bindValues[3],
													this.bindValues[4], this.bindValues[5],
													this.bindValues[6], this.bindValues[7],
													this.bindValues[8], this.bindValues[9],
													this.bindValues[10], this.bindValues[11],
													this.bindValues[12], this.bindValues[13]);
        rs = pstmt.executeQuery();
        if(rs.next()) {
            return rs.getClob("PCTXT");
		}
		return null;
	}
}
