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
 * Represents a PeopleTools menu definition.
 */
public class Menu {

  private static Logger log = LogManager.getLogger(Menu.class.getName());

  private final String ptMENUNAME;
  private final List<MenuItem> menuItems;

  /**
   * Create a representation of the menu defn with the
   * provided name.
   * @param menuname name of the menu defn
   */
  public Menu(final String menuname) {
    this.ptMENUNAME = menuname;
    this.menuItems = new ArrayList<MenuItem>();

    OPSStmt ostmt = StmtLibrary.getStaticSQLStmt("query.PSMENUDEFN",
        new String[]{this.ptMENUNAME});
    OPSResultSet rs = ostmt.executeQuery();

    //Do nothing with record for now.
    rs.next();
    rs.close();
    ostmt.close();

    ostmt = StmtLibrary.getStaticSQLStmt("query.PSMENUITEM",
        new String[]{this.ptMENUNAME});
    rs = ostmt.executeQuery();

    while(rs.next())  {
      if (rs.getInt("ITEMTYPE") == 5) {
        MenuItem item = new MenuItem();
        item.BARNAME = rs.getString("BARNAME");
        item.ITEMNAME = rs.getString("ITEMNAME");
        item.PNLGRPNAME = rs.getString("PNLGRPNAME");
        item.MARKET = rs.getString("MARKET");
        this.menuItems.add(item);
      }
    }

    rs.close();
    ostmt.close();
  }

  public void loadReferencedComponents() {
    log.debug("Loading all components referenced by Menu.{}...",
      this.ptMENUNAME);
    for (MenuItem item : this.menuItems) {
      DefnCache.getComponent(item.PNLGRPNAME, item.MARKET);
    }
    log.debug("Done loading all components referenced by Menu.{}",
      this.ptMENUNAME);
  }

  /**
   * Retrieve the MENUNAME for this Menu.
   * @return the menu's MENUNAME value
   */
  public String getMenuName() {
    return this.ptMENUNAME;
  }

  private class MenuItem {
    private String BARNAME, ITEMNAME, PNLGRPNAME, MARKET;
  }
}

