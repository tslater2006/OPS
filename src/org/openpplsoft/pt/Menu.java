/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.runtime.*;
import org.openpplsoft.sql.*;

/**
 * Represents a PeopleTools menu definition.
 */
public class Menu {

  private static Logger log = LogManager.getLogger(Menu.class.getName());

  private String ptMENUNAME;

  /**
   * Create a representation of the menu defn with the
   * provided name.
   * @param menuname name of the menu defn
   */
  public Menu(final String menuname) {
    this.ptMENUNAME = menuname;

    OPSStmt ostmt = StmtLibrary.getStaticSQLStmt("query.PSMENUDEFN",
        new String[]{this.ptMENUNAME});
    ResultSet rs = null;

    try {
      rs = ostmt.executeQuery();
      //Do nothing with record for now.
      rs.next();
      rs.close();
      ostmt.close();

      ostmt = StmtLibrary.getStaticSQLStmt("query.PSMENUITEM",
          new String[]{this.ptMENUNAME});
      rs = ostmt.executeQuery();
      //Do nothing with records for now.
      rs.next();
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

  /**
   * Retrieve the MENUNAME for this Menu.
   * @return the menu's MENUNAME value
   */
  public String getMenuName() {
    return this.ptMENUNAME;
  }
}

