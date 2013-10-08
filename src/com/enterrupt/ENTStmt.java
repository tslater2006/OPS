package com.enterrupt;

import java.sql.Connection;
import java.util.ArrayList;
import java.sql.PreparedStatement;

class ENTStmt {
    public String sql;
    public ArrayList<String> bindVals;

    public ENTStmt(String sql) {
        this.sql = sql;
        this.bindVals = new ArrayList<String>();
    }

    public PreparedStatement generatePreparedStmt(Connection conn) throws Exception {
        PreparedStatement pstmt = conn.prepareStatement(this.sql);
        for(int i=0; i < this.bindVals.size(); i++) {
            pstmt.setString(i+1, this.bindVals.get(i));
        }
        StmtLibrary.emittedStmts.push(this);
        return pstmt;
    }
}
