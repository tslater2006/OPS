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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.runtime.*;
import org.openpplsoft.sql.*;

/**
 * Represents a PeopleTools message set definition.
 */
public class MsgSet {

  private static Logger log = LogManager.getLogger(MsgSet.class.getName());

  private int ptMESSAGE_SET_NBR;
  private Map<Integer, String> messages;

  /**
   * Create a representation of the msg set defn with the
   * provided msg set nbr.
   * @param msgSetNbr number of the message set
   */
  public MsgSet(final int msgSetNbr) {
    this.ptMESSAGE_SET_NBR = msgSetNbr;

    OPSStmt ostmt = StmtLibrary.getStaticSQLStmt("query.PSMSGSETDEFN",
        new String[]{""+this.ptMESSAGE_SET_NBR});
    ResultSet rs = null;

    try {
      rs = ostmt.executeQuery();
      //Do nothing with record for now.
      rs.next();
      rs.close();
      ostmt.close();

      ostmt = StmtLibrary.getStaticSQLStmt("query.PSMSGCATDEFN",
          new String[]{""+this.ptMESSAGE_SET_NBR});
      rs = ostmt.executeQuery();

      this.messages = new HashMap<Integer, String>();
      while(rs.next())  {
        this.messages.put(rs.getInt("MESSAGE_NBR"),
            rs.getString("MESSAGE_TEXT"));
      }
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

  /**
   * Retrieve the MESSAGE_SET_NBR for this MsgSet.
   * @return the message set's number
   */
  public int getMsgSetNbr() {
    return this.ptMESSAGE_SET_NBR;
  }

  public String getMessage(final int msgNbr) {
    return this.messages.get(msgNbr);
  }
}

