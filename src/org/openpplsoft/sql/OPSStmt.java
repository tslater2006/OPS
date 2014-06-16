/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.HashMap;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.runtime.*;

/**
 * Used for all SQL emissions emitted by
 * the OPS runtime.
 */
public class OPSStmt extends SQLStmt {

  private static Logger log =
      LogManager.getLogger(OPSStmt.class.getName());

  private static int stmtCounter;

  /**
   * @param sql the sql statement to be executed
   */
  public OPSStmt(final String sql) {
    super(sql.trim());
  }

  public PreparedStatement generateEnforcedPreparedStmt(
      final Connection conn) {
    final PreparedStatement pstmt = this.generatePreparedStmt(conn);
    TraceFileVerifier.enforceEmission(this);
    return pstmt;
  }

  public PreparedStatement generateUnenforcedPreparedStmt(
      final Connection conn) {
    final PreparedStatement pstmt = this.generatePreparedStmt(conn);
    TraceFileVerifier.submitUnenforcedEmission(this);
    return pstmt;
  }

  private PreparedStatement generatePreparedStmt(
      final Connection conn) {
    try {
      final PreparedStatement pstmt = conn.prepareStatement(this.sql);
      for (Map.Entry<Integer, String> cursor : this.bindVals.entrySet()) {
        pstmt.setString(cursor.getKey(), cursor.getValue());
      }
      return pstmt;
    } catch (final java.sql.SQLException sqle) {
      log.fatal(sqle.getMessage(), sqle);
      System.exit(ExitCode.FAILED_TO_CREATE_PSTMT_FROM_CONN.getCode());
    }
    return null;
  }
}

