/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Blob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.runtime.*;
import org.openpplsoft.sql.*;

/**
 * Represents a PeopleTools SQL definition.
 */
public class SQL {

  private static Logger log = LogManager.getLogger(SQL.class.getName());

  private String ptSQLID;

  /*
   * Although this value can vary among SQL objects, the most
   * common appears to be 0 representing "SQL Object referenced from elsewhere"
   * (http://www.go-faster.co.uk/peopletools/pssqldefn.htm). If you have issues
   * with queries relating to retrieval of SQL defns, it's possible a value
   * for SQLTYPE other than 0 could be involved.
   */
  private final String ptSQLTYPE = "0";


  /**
   * Create a representation of the SQL defn with the
   * provided SQL defn name
   * @param sqlDefnName the SQLID of the SQL defn
   */
  public SQL(final String sqlDefnName) {
    this.ptSQLID = sqlDefnName;

    final String[] bindVals = new String[]{this.ptSQLID, this.ptSQLTYPE};
    OPSStmt ostmt = StmtLibrary.getStaticSQLStmt("query.PSSQLDEFN", bindVals);
    ResultSet rs = null;

    try {
      rs = ostmt.executeQuery();
      //Do nothing with record for now.
      rs.next();
      rs.close();
      ostmt.close();

/*      ostmt = StmtLibrary.getStaticSQLStmt("query.PSCONTENT", bindVals);
      rs = ostmt.executeQuery();

      if(rs.next())  {
        final Blob blob = rs.getBlob("CONTDATA");

        StringBuilder builder = new StringBuilder();
        byte[] arr = blob.getBytes(1, (int) blob.length());
        for (byte b : arr) {
          if (b > 0) {
            builder.append(Character.toString((char) b));
          }
        }

        this.ptCONTDATA = builder.toString();

        if (rs.next()) {
          throw new OPSVMachRuntimeException("Multiple records found for SQL defn; "
              + "expected only one.");
        }
      } else {
        throw new OPSVMachRuntimeException("No records found; unable to get SQL defn.");
      }*/
    } catch (final java.sql.SQLException sqle) {
      log.fatal(sqle.getMessage(), sqle);
      System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
    } finally {
      try {
        if (rs != null) { rs.close(); }
        if (ostmt != null) { ostmt.close(); }
      } catch (final java.sql.SQLException sqle) {
        log.warn("Unable to close rs and/or ostmt in finally block.");
      }
    }
  }
}

