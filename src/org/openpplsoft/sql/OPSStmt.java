/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

  private String[] bindValList;
  private EmissionType emissionType;
  private PreparedStatement pstmt;

  /**
   * An OPS SQL emission can either be enforced against the trace
   * file or it can be unenforced.
   */
  public static enum EmissionType {
     ENFORCED, UNENFORCED
  }

  public OPSStmt(final String sql, final String[] bVals,
      final EmissionType eType) {

    super(sql.trim());
    this.emissionType = eType;

    for (int i = 0; i < bVals.length; i++) {
      this.bindVals.put(i+1, bVals[i]);
    }

    try {
      this.pstmt = StmtLibrary.getConnection().prepareStatement(this.sql);
      for (Map.Entry<Integer, String> cursor : this.bindVals.entrySet()) {
        pstmt.setString(cursor.getKey(), cursor.getValue());
      }
    } catch (final java.sql.SQLException sqle) {
      log.fatal(sqle.getMessage(), sqle);
      System.exit(ExitCode.FAILED_TO_CREATE_PSTMT_FROM_CONN.getCode());
    }
  }

  public ResultSet executeQuery() throws java.sql.SQLException {

    if(this.emissionType == EmissionType.ENFORCED) {
      TraceFileVerifier.submitEnforcedEmission(this);
    } else {
      TraceFileVerifier.submitUnenforcedEmission(this);
    }

    return this.pstmt.executeQuery();
  }

  public void close() throws java.sql.SQLException {
    this.pstmt.close();
  }
}

