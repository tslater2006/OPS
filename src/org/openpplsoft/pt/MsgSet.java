/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.pt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    OPSResultSet rs = ostmt.executeQuery();

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

    rs.close();
    ostmt.close();
  }

  /**
   * Retrieve the MESSAGE_SET_NBR for this MsgSet.
   * @return the message set's number
   */
  public int getMsgSetNbr() {
    return this.ptMESSAGE_SET_NBR;
  }

  public boolean hasMsgNbr(final int msgNbr) {
    return this.messages.containsKey(msgNbr);
  }

  public String getMessage(final int msgNbr, final String[] msgBindVals) {

    String msg = this.messages.get(msgNbr);

    // This pattern uses negative lookbehind to exclude escaped percent
    // signs (i.e., "%%1" is NOT a bind index) and
    // allows for matches at the very beginning of the string.
    final Pattern strBindIdxPattern = Pattern.compile("(?<!%)%\\d+");
    final Matcher strBindIdxMatcher = strBindIdxPattern.matcher(msg);

    final StringBuffer msgSb = new StringBuffer();
    boolean bindIdxFound = false;
    while (strBindIdxMatcher.find()) {
      bindIdxFound = true;

      // Subtract 1 b/c bind indices start at 1 in PS, Java arrays are 0-based.
      final int bindIdx = Integer.parseInt(strBindIdxMatcher.group().substring(1)) - 1;
      if (bindIdx >= msgBindVals.length) {
        throw new OPSVMachRuntimeException("Bind idx in message ("
            + bindIdx + ") does not map to a value in the bind value list: "
            + msgBindVals);
      }

      // Replace the bind index with the corresponding bind value.
      strBindIdxMatcher.appendReplacement(msgSb, msgBindVals[bindIdx]);
    }

    // If one or more bind indices was replaced with a value, update the msg string.
    if (bindIdxFound) {
      strBindIdxMatcher.appendTail(msgSb);
      msg = msgSb.toString();
    }

    // Escaped '%' chars must be translated into a single '%'.
    final Pattern escPctPattern = Pattern.compile("^[%]\\d+|[^%]%\\d+");
    final Matcher escPctMatcher = escPctPattern.matcher(msg);
    msg = escPctMatcher.replaceAll("%");

    // Detect presence of escaped line breaks; these must be handled somehow.
    final Pattern escLbPattern = Pattern.compile("%\\\\");
    final Matcher escLbMatcher = escLbPattern.matcher(msg);
    if (escLbMatcher.find()) {
      throw new OPSVMachRuntimeException("TODO: Handle escaped line breaks in the "
          + "text of messages (see PT documentation for MsgGetText).");
    }

    return msg;
  }
}

