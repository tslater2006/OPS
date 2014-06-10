/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package com.enterrupt.sql;

import java.sql.Connection;
import java.util.HashMap;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import com.enterrupt.runtime.*;
import org.apache.logging.log4j.*;

public class ENTStmt extends SQLStmt {

  private static Logger log = LogManager.getLogger(ENTStmt.class.getName());
  private static int stmtCounter = 0;

  public ENTStmt(String sql) {
    super(sql.trim());
  }

  public PreparedStatement generateEnforcedPreparedStmt(Connection conn) {
    PreparedStatement pstmt = generatePreparedStmt(conn);
    TraceFileVerifier.enforceEmission(this);
    return pstmt;
  }

  public PreparedStatement generateUnenforcedPreparedStmt(Connection conn) {
    PreparedStatement pstmt = generatePreparedStmt(conn);
    TraceFileVerifier.submitUnenforcedEmission(this);
    return pstmt;
  }

  private PreparedStatement generatePreparedStmt(Connection conn) {
    try {
      PreparedStatement pstmt = conn.prepareStatement(this.sql);
      for(Map.Entry<Integer, String> cursor : this.bindVals.entrySet()) {
        pstmt.setString(cursor.getKey(), cursor.getValue());
      }
      return pstmt;
    } catch(java.sql.SQLException sqle) {
      log.fatal(sqle.getMessage(), sqle);
      System.exit(ExitCode.FAILED_TO_CREATE_PSTMT_FROM_CONN.getCode());
    }
    return null;
  }
}

