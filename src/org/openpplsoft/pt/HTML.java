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
 * Represents a PeopleTools HTML definition.
 */
public class HTML {

  private static Logger log = LogManager.getLogger(HTML.class.getName());

  private String ptCONTNAME;
  private String ptCONTDATA;
  private final String ptCONTTYPE = "4";

  /**
   * Create a representation of the HTML defn with the
   * provided HTML defn name
   * @param htmlDefnName the CONTNAME of the HTML defn
   */
  public HTML(final String htmlDefnName) {
    this.ptCONTNAME = htmlDefnName;

    final String[] bindVals = new String[]{this.ptCONTNAME, this.ptCONTTYPE};
    OPSStmt ostmt = StmtLibrary.getStaticSQLStmt("query.PSCONTDEFN", bindVals);
    ResultSet rs = null;

    try {
      rs = ostmt.executeQuery();
      //Do nothing with record for now.
      rs.next();
      rs.close();
      ostmt.close();

      ostmt = StmtLibrary.getStaticSQLStmt("query.PSCONTENT", bindVals);
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
          throw new OPSVMachRuntimeException("Multiple records found for HTML defn; "
              + "expected only one.");
        }
      } else {
        throw new OPSVMachRuntimeException("No records found; unable to get HTML defn.");
      }
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

  public String getHTMLText() {
    return this.ptCONTDATA;
  }
}

