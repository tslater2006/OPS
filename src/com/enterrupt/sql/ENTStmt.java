package com.enterrupt.sql;

import java.sql.Connection;
import java.util.HashMap;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import com.enterrupt.runtime.ExitCode;
import org.apache.logging.log4j.*;

public class ENTStmt extends SQLStmt {

	private static Logger log = LogManager.getLogger(ENTStmt.class.getName());
	private static int stmtCounter = 0;

    public ENTStmt(String sql) {
        super(sql.trim());
    }

    public PreparedStatement generatePreparedStmt(Connection conn) {

		try {
	        PreparedStatement pstmt = conn.prepareStatement(this.sql);
   		    for(Map.Entry<Integer, String> cursor : this.bindVals.entrySet()) {
         		pstmt.setString(cursor.getKey(), cursor.getValue());
        	}

			log.debug("Stmt counter: {}", stmtCounter++);
			log.debug(this);
        	StmtLibrary.emittedStmts.add(this);
        	return pstmt;

		} catch(java.sql.SQLException sqle) {
			log.fatal(sqle.getMessage(), sqle);
			System.exit(ExitCode.FAILED_TO_CREATE_PSTMT_FROM_CONN.getCode());
		}

		return null;
    }
}

