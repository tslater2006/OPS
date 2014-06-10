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
import org.openpplsoft.sql.StmtLibrary;

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

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      pstmt = StmtLibrary.getPSMENUDEFN(this.ptMENUNAME);
      rs = pstmt.executeQuery();
      //Do nothing with record for now.
      rs.next();
      rs.close();
      pstmt.close();

      pstmt = StmtLibrary.getPSMENUITEM(this.ptMENUNAME);
      rs = pstmt.executeQuery();
      //Do nothing with records for now.
      rs.next();
    } catch (final java.sql.SQLException sqle) {
      log.fatal(sqle.getMessage(), sqle);
      System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
    } finally {
      try {
        if (rs != null) { rs.close(); }
        if (pstmt != null) { pstmt.close(); }
      } catch (final java.sql.SQLException sqle) {
        log.warn("Unable to close rs and/or pstmt in finally block.");
      }
    }
  }
}

