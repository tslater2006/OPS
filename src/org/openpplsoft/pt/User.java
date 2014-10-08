/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.runtime.*;
import org.openpplsoft.sql.*;

/**
 * Represents a PeopleSoft user.
 */
public class User {

  private static Logger log = LogManager.getLogger(User.class.getName());

  private String oprid, emplid, oprclass;

  public User(final String oprid) {
    this.oprid = oprid;

    OPSStmt ostmt = StmtLibrary.getStaticSQLStmt("query.PSOPRDEFN",
        new String[]{this.oprid});
    ResultSet rs = null;

    try {
      rs = ostmt.executeQuery();
      rs.next();
      this.oprid = rs.getString("OPRID");
      this.emplid = rs.getString("EMPLID");
      this.oprclass = rs.getString("OPRCLASS");
      rs.close();
      ostmt.close();
    } catch (final java.sql.SQLException sqle) {
      throw new OPSVMachRuntimeException(sqle.getMessage(), sqle);
    } finally {
      try {
        if (rs != null) { rs.close(); }
        if (ostmt != null) { ostmt.close(); }
      } catch (final java.sql.SQLException sqle) {
        log.warn("Unable to close rs and/or ostmt in finally block.");
      }
    }
  }

  public String getOprid() {
    return this.oprid;
  }

  public String getEmplid() {
    return this.emplid;
  }

  public String getOprClass() {
    return this.oprclass;
  }
}

