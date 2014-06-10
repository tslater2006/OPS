/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.openpplsoft.sql.StmtLibrary;
import org.apache.logging.log4j.*;
import org.openpplsoft.runtime.*;

public class Menu {

  public String MENUNAME;

  private static Logger log = LogManager.getLogger(Menu.class.getName());

  public Menu(String menuname) {
    this.MENUNAME = menuname;

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      pstmt = StmtLibrary.getPSMENUDEFN(this.MENUNAME);
      rs = pstmt.executeQuery();
      rs.next();      //Do nothing with record for now.
      rs.close();
      pstmt.close();

      pstmt = StmtLibrary.getPSMENUITEM(this.MENUNAME);
      rs = pstmt.executeQuery();
      rs.next();      //Do nothing with records for now.
    } catch(java.sql.SQLException sqle) {
      log.fatal(sqle.getMessage(), sqle);
      System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
    } finally {
      try {
        if(rs != null) { rs.close(); }
        if(pstmt != null) { pstmt.close(); }
      } catch(java.sql.SQLException sqle) {}
    }
  }
}

