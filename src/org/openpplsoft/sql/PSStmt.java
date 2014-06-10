/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.sql;

import java.sql.Connection;
import java.util.HashMap;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PSStmt extends SQLStmt {

  private static Pattern bindIdxPattern;
  public String originalStmt;
  public int line_nbr;

  static {
    // Note: this regex uses positve lookbehind and lookahead.
    bindIdxPattern = Pattern.compile("(?<=\\s*)(:\\d+)(?=\\s?)");
  }

  public PSStmt(String sql, int line_nbr) {
    super(sql.trim());

    this.line_nbr = line_nbr;

    Matcher m = bindIdxPattern.matcher(sql);
    this.originalStmt = sql;
    this.sql =  m.replaceAll("?").trim();
  }
}
