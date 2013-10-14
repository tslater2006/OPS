package com.enterrupt.sql;

import java.sql.Connection;
import java.util.HashMap;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ENTStmt extends SQLStmt {

    public ENTStmt(String sql) {
        super(sql.trim());
    }

    public PreparedStatement generatePreparedStmt(Connection conn) throws Exception {
        PreparedStatement pstmt = conn.prepareStatement(this.sql);
        for(Map.Entry<Integer, String> cursor : this.bindVals.entrySet()) {
            pstmt.setString(cursor.getKey(), cursor.getValue());
        }
        StmtLibrary.emittedStmts.add(this);
        return pstmt;
    }
}

