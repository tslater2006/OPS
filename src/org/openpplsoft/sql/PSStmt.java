/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.HashMap;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a SQL statement issued by
 * PeopleSoft (i.e., via trace file).
 */
public class PSStmt extends SQLStmt {

  private static Pattern bindIdxPattern;

  private String originalStmt;
  private int lineNbr;

  static {
    // Note: this regex uses positve lookbehind and lookahead.
    bindIdxPattern = Pattern.compile("(?<=\\s*)(:\\d+)(?=\\s?)");
  }

  /**
   * @param s the SQL statement issued by PeopleSoft
   * @param l the line number in the trace file on
   *   which the issued statement is located
   */
  public PSStmt(final String s, final int l) {
    super(s.trim());

    this.lineNbr = l;

    final Matcher m = bindIdxPattern.matcher(s);
    this.originalStmt = s;
    this.sql = m.replaceAll("?").trim();
  }

  /**
   * @return the SQL statement, as it is found
   *   in the trace file (verbatim).
   */
  public String getOriginalStmt() {
    return this.originalStmt;
  }
}
