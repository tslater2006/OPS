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

        PreparedStatement pstmt;
        ResultSet rs;

        pstmt = StmtLibrary.getPSRECDEFN(this.RECNAME);
		System.out.println(this.RECNAME);
        rs = pstmt.executeQuery();
        rs.next(); // Do nothing with record for now.
        rs.close();
        pstmt.close();
    }
}

