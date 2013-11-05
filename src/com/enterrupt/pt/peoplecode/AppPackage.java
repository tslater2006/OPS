package com.enterrupt.pt_objects;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import com.enterrupt.sql.StmtLibrary;

public class AppPackage {

	public String packageName;

	private boolean hasDiscoveredAppClassPC = false;

	public AppPackage(String pName) {
		this.packageName = pName;
	}

	public void discoverAppClassPC() throws Exception {

		if(this.hasDiscoveredAppClassPC) { return; }
		this.hasDiscoveredAppClassPC = true;

        PreparedStatement pstmt;
        ResultSet rs;

        pstmt = StmtLibrary.getPSPCMPROG_RecordPCList(PSDefn.APP_PACKAGE, this.packageName);
        rs = pstmt.executeQuery();

        /**
         * TODO: BE VERY CAREFUL HERE; not only is this not ordered by PROGSEQ, but
         * there may be multiple entries per program if it is greater than 28,000 bytes.
         * Keep those considerations in mind before using the records returned here.
         */
        rs.next(); // Do nothing with records for now.
        rs.close();
        pstmt.close();
	}
}
