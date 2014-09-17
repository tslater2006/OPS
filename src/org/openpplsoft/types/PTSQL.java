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

  private static Map<String, Method> ptMethodTable;
  private static Pattern dtPattern, datePattern, dotPattern;

  private SQL sqlDefn;
  private PTPrimitiveType[] bindVals;
  private OPSStmt ostmt;
  private ResultSet rs;
  private ResultSetMetaData rsMetaData;

  static {
    final String PT_METHOD_PREFIX = "PT_";

    // cache pointers to PeopleTools Record methods.
    final Method[] methods = PTSQL.class.getMethods();
    ptMethodTable = new HashMap<String, Method>();
    for (Method m : methods) {
      if (m.getName().indexOf(PT_METHOD_PREFIX) == 0) {
        ptMethodTable.put(m.getName().substring(
            PT_METHOD_PREFIX.length()), m);
      }
    }
  }

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

    try {
      this.rs = ostmt.executeQuery();
      this.rsMetaData = rs.getMetaData();
    } catch (final java.sql.SQLException sqle) {
      log.fatal(sqle.getMessage(), sqle);
      System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
    }
  }

  @Override
  public PTType dotProperty(final String s) {
    return null;
  }

  @Override
  public Callable dotMethod(final String s) {
    if (ptMethodTable.containsKey(s)) {
      return new Callable(ptMethodTable.get(s), this);
    }
    return null;
  }

  /**
   * Returns True if a row was fetched; false otherwise.
   */
  public void PT_Fetch() {
    final List<PTType> args = Environment.getArgsFromCallStack();
    if (args.size() == 0) {
      throw new OPSVMachRuntimeException("Expected at least one arg.");
    }

    try {
      if (rs.next()) {
        if (this.rsMetaData.getColumnCount() != args.size()) {
          throw new OPSVMachRuntimeException("Fetch failed; the number of args "
              + "passed to Fetch must equal the number of columns returned by the "
              + "underlying SQL statement.");
        }

        for (int i = 0; i < args.size() && i < this.rsMetaData.getColumnCount(); i++) {
          final PTType arg = args.get(i);
          if (!(arg instanceof PTReference)) {
            throw new OPSVMachRuntimeException("Fetch failed; expected a reference "
                + "(variable) as one of the arguments but found: " + arg);
          }

          /**
           * TODO(mquinn): Because we cannot rely on the arg's type (can be Any)
           * to determine how to read the underlying field from the database,
           * I will likely need to access Field metadata to determine exactly
           * which raw PTType should be created. Until then, I am passing only
           * strings. This works for now but will need to change.
           */
          final String colName = this.rsMetaData.getColumnName(i+1);
          final String colTypeName = this.rsMetaData.getColumnTypeName(i+1);
          switch(colTypeName) {
            case "VARCHAR":
              GlobalFnLibrary.assign(arg, new PTString(rs.getString(colName)));
              break;
            case "VARCHAR2":
              GlobalFnLibrary.assign(arg, new PTString(rs.getString(colName)));
              break;
            default:
              throw new OPSVMachRuntimeException("Unexpected colTypeName in Fetch: "
                  + colTypeName);
          }
        }

        Environment.pushToCallStack(new PTBoolean(true));
      } else {
        Environment.pushToCallStack(new PTBoolean(false));
      }
    } catch (final java.sql.SQLException sqe) {
      throw new OPSVMachRuntimeException(sqe.getMessage(), sqe);
    }
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(":").append(this.sqlDefn);
    b.append(",bindVals=").append(Arrays.toString(this.bindVals));
    return b.toString();
  }
}
