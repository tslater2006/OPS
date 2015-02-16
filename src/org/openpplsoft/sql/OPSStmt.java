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

  private final EmissionType emissionType;
  private final  PreparedStatement pstmt;

  private OPSResultSet rs;

  /**
   * An OPS SQL emission can either be enforced against the trace
   * file or it can be unenforced.
   */
  public static enum EmissionType {
     ENFORCED, UNENFORCED
  }

  /**
   * Creates an OPSStmt instance.
   * @param sql the SQL statement to be executed
   * @param bVals the bind vals to attach at execution time
   * @param eType the emission type (enforced or unenforced)
   */
  public OPSStmt(final String sql, final String[] bVals,
      final EmissionType eType) {

    super(sql.trim());
    this.emissionType = eType;

    for (int i = 0; i < bVals.length; i++) {
      /*
       * IMPORTANT NOTE: For all empty string bind values, I am
       * replacing them with a single character blank string, as that
       * is how PS represents NULL in the database. You may run into
       * issues in the event that a query explicitly wants to query
       * using an empty string. In that case, for every record being queried/filled/etc.,
       * I believe you will need to match each bind value to the appropriate field and determine
       * if the field is required according to PS; if it is, those should be converted to
       * from the empty string, and all other fields should be left alone.
       * TODO(mquinn): keep this in mind.
       */
      if (bVals[i].equals("")) {
        this.setBindVal(i + 1, " ");
      } else {
        this.setBindVal(i + 1, bVals[i]);
      }
    }

    try {
      this.pstmt = StmtLibrary.getConnection().prepareStatement(this.getSql());
      for (Map.Entry<Integer, String> cursor : this.getBindVals().entrySet()) {
        this.pstmt.setString(cursor.getKey(), cursor.getValue());
      }
    } catch (final java.sql.SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }

  /**
   * Executes the query represented by this OPSStmt.
   * @return the OPSResultSet containing the query results
   * @throws java.sql.SQLException if execution of underlying PreparedStatement
   *    results in an error.
   */
  public OPSResultSet executeQuery() {

    if (this.emissionType == EmissionType.ENFORCED) {
      TraceFileVerifier.submitEnforcedEmission(this);
    } else {
      TraceFileVerifier.submitUnenforcedEmission(this);
    }

    if (this.rs != null) {
      throw new OPSVMachRuntimeException("An OPSResultSet has already been associated "
          + "with this OPSStmt, expected null.");
    }

    try {
      this.rs = new OPSResultSet(this.pstmt.executeQuery());
    } catch (final java.sql.SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
    return this.rs;
  }

  /**
   * Closes the underlying PreparedStatement.
   * @throws java.sql.SQLException if closing of underlying PreparedStatement
   *    results in an error.
   */
  public void close() {
    try {
      this.pstmt.close();
      if (this.rs != null) {
        rs.close();
      }
    } catch (final java.sql.SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    }
  }
}

