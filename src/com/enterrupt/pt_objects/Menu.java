package com.enterrupt.pt_objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.enterrupt.sql.StmtLibrary;

public class Menu {

    public String MENUNAME;

    public Menu(String menuname) {
        this.MENUNAME = menuname;
    }

    public void loadInitialMetadata() throws Exception {

        PreparedStatement pstmt;
        ResultSet rs;

        pstmt = StmtLibrary.getPSMENUDEFN(this.MENUNAME);
        rs = pstmt.executeQuery();
        rs.next();      //Do nothing with record for now.
        rs.close();
        pstmt.close();

        pstmt = StmtLibrary.getPSMENUITEM(this.MENUNAME);
        rs = pstmt.executeQuery();
        while(rs.next()) {
            //Do nothing with records for now.
        }
        rs.close();
        pstmt.close();
    }
}

