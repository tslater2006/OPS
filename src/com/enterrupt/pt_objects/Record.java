package com.enterrupt.pt_objects;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import com.enterrupt.BuildAssistant;
import com.enterrupt.sql.StmtLibrary;

public class Record {

    public String RECNAME;

    public Record(String recname) {
        this.RECNAME = recname;
    }

    public void loadInitialMetadata() throws Exception {

		// Do not contain if record definition has been cached.
		if(BuildAssistant.recDefnCache.get(this.RECNAME) != null) {
			return;
		}

        PreparedStatement pstmt;
        ResultSet rs;

        pstmt = StmtLibrary.getPSRECDEFN(this.RECNAME);
		System.out.println(this.RECNAME);
        rs = pstmt.executeQuery();
        rs.next(); // Do nothing with record for now.
        rs.close();
        pstmt.close();

        pstmt = StmtLibrary.getPSDBFIELD_PSRECFIELD_JOIN(this.RECNAME);
        rs = pstmt.executeQuery();
        while(rs.next()) {
            // Do nothing with records for now.
        }
        rs.close();
        pstmt.close();

        pstmt = StmtLibrary.getPSDBFLDLBL(this.RECNAME);
        rs = pstmt.executeQuery();
        while(rs.next()) {
            // Do nothing with records for now.
        }
        rs.close();
        pstmt.close();

		// Cache the record definition.
		BuildAssistant.cacheRecord(this);
    }
}

