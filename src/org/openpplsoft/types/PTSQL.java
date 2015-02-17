/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.lang.reflect.Method;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.sql.*;

/**
 * Represents a PeopleTools SQL object.
 */
public final class PTSQL extends PTObjectType {

  private static Logger log = LogManager.getLogger(PTSQL.class.getName());

  private static Pattern dtPattern, datePattern, dotPattern;

  private final SQL sqlDefn;
  private final PTPrimitiveType[] bindVals;
  private final OPSStmt ostmt;

  private OPSResultSet rs;

  /**
   * Creates a new SQL object that is attached
   * to a SQL defn.
   * @param s the SQL defn to attach
   * @param b the bind values to attach
   */
  public PTSQL(final PTSQLTypeConstraint origTc, final SQL s,
        final PTPrimitiveType[] b) {
    super(origTc);

    this.sqlDefn = s;
    this.bindVals = b;

    // Create an OPSStmt; this will be an enforced emission since all
    // dynamic SQL is enforced. Must provide array of String bind values.
    final String[] strBindVals = new String[this.bindVals.length];
    for (int i = 0; i < this.bindVals.length; i++) {
      strBindVals[i] = this.bindVals[i].readAsString();
    }
    this.ostmt = StmtLibrary.prepareSqlFromSQLDefn(this.sqlDefn, strBindVals);
  }

  /**
   * IMPORTANT: DO NOT close ostmt or rs in this method. This method is called
   * by GetSQL, and later calls to Fetch will expect the ResultSet to be
   * open and ready for reading.
   */
  public void executeSql() {

    if (this.rs != null) {
      throw new OPSVMachRuntimeException("Expected rs to be null before running "
          + "executeSql on PTSQL object.");
    }

    this.rs = ostmt.executeQuery();
  }

  @Override
  public PTType dotProperty(final String s) {
    return null;
  }

  /**
   * Returns True if a row was fetched; false otherwise.
   */
  @PeopleToolsImplementation
  public void Fetch() {

    final List<PTType> args = Environment.getArgsFromCallStack();
    if (args.size() == 0) {
      throw new OPSVMachRuntimeException("Expected at least one arg.");
    }

    if (rs.next()) {
      if (this.rs.getColumnCount() != args.size()) {
        throw new OPSVMachRuntimeException("Fetch failed; the number of args "
            + "passed to Fetch must equal the number of columns returned by the "
            + "underlying SQL statement.");
      }

      for (int colIdx = 1;
          colIdx <= args.size() && colIdx <= this.rs.getColumnCount(); colIdx++) {

        final PTType outVar = args.get(colIdx - 1);
        final PTPrimitiveType dbVal = rs.getTypeCompatibleValue(colIdx,
            outVar.getOriginatingTypeConstraint());

        Environment.assign(outVar, dbVal);
      }

      Environment.pushToCallStack(new PTBoolean(true));
    } else {
      Environment.pushToCallStack(new PTBoolean(false));
    }

    /*
     * IMPORTANT: Do NOT close the underlying result set or statement here;
     * Fetch can be called multiple times by the caller.
     * TODO(mquinn): Ensure that eventually, the underlying result set and
     * statement eventually are closed.
     */
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(':').append(this.sqlDefn);
    b.append(",bindVals=").append(Arrays.toString(this.bindVals));
    return b.toString();
  }
}
