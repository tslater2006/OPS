package com.enterrupt.pt.peoplecode;

import java.sql.*;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.runtime.*;
import com.enterrupt.pt.*;
import org.apache.logging.log4j.*;

public class AppPackage {

	public String packageName;

	private static Logger log = LogManager.getLogger(AppPackage.class.getName());

	private boolean hasDiscoveredAppClassPC = false;

	public AppPackage(String pName) {
		this.packageName = pName;
	}

	public void discoverAppClassPC() {

		if(this.hasDiscoveredAppClassPC) { return; }
		this.hasDiscoveredAppClassPC = true;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

		try {
	        pstmt = StmtLibrary.getPSPCMPROG_RecordPCList(PSDefn.APP_PACKAGE, this.packageName);
   		    rs = pstmt.executeQuery();

	        /**
    	     * TODO: BE VERY CAREFUL HERE; not only is this not ordered by PROGSEQ, but
       		 * there may be multiple entries per program if it is greater than 28,000 bytes.
	         * Keep those considerations in mind before using the records returned here.
    	     */
	        rs.next(); // Do nothing with records for now.

        } catch(java.sql.SQLException sqle) {
            log.fatal(sqle.getMessage(), sqle);
            System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
        } finally {
            try {
                if(rs != null) { rs.close(); }
                if(pstmt != null) { pstmt.close(); }
            } catch(java.sql.SQLException sqle) {}
        }
	}
}
