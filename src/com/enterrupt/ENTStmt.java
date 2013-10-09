package com.enterrupt;

import java.sql.Connection;
import java.util.HashMap;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @sql : bind tokens should be non-numeric (i.e., "?")
 *		  to ensure equals() works correctly.
 */
class SQLStmt {
	public String sql;
    public HashMap<Integer, String> bindVals;

	public SQLStmt(String sql) {
        this.sql = sql;
        this.bindVals = new HashMap<Integer, String>();
	}
}

class ENTStmt extends SQLStmt {

    public ENTStmt(String sql) {
		super(sql);
    }

    public PreparedStatement generatePreparedStmt(Connection conn) throws Exception {
        PreparedStatement pstmt = conn.prepareStatement(this.sql);
        for(Map.Entry<Integer, String> cursor : this.bindVals.entrySet()) {
            pstmt.setString(cursor.getKey(), cursor.getValue());
        }
        StmtLibrary.emittedStmts.push(this);
        return pstmt;
    }
}

class PSStmt extends SQLStmt {
	// Note: this regex uses positve lookbehind and lookahead.
	private final String BIND_IDX_REGEX = "(?<=\\s)(:\\d+)(?=\\s?)";

	public PSStmt(String sql) {
		super(sql);

		Pattern bindIdxPattern = Pattern.compile(BIND_IDX_REGEX);
		Matcher m = bindIdxPattern.matcher(sql);
		sql = m.replaceAll("?");

		System.out.println(sql);
		this.sql = sql;
	}
}
