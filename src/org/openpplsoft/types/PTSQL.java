/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.lang.reflect.Method;

import java.sql.ResultSet;

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
    } catch (final java.sql.SQLException sqle) {
      log.fatal(sqle.getMessage(), sqle);
      System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
    }
  }

  @Override
  public PTType dotProperty(final String s) {
    throw new OPSDataTypeException("dotProperty on PTSQL is not supported.");
  }

  @Override
  public Callable dotMethod(final String s) {
    throw new OPSDataTypeException("dotMethod on PTSQL is not supported.");
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(":").append(this.sqlDefn);
    b.append(",bindVals=").append(Arrays.toString(this.bindVals));
    return b.toString();
  }
}
