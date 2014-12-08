/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.lang.reflect.Method;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.buffers.*;
import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.sql.*;
import org.openpplsoft.trace.*;

/**
 * Represents a PeopleTools standalone (not in component buffer) rowset.
 */
public final class PTStandaloneRowset extends PTRowset<PTStandaloneRow> {

  private static Logger log = LogManager.getLogger(
      PTStandaloneRowset.class.getName());

  private static Map<String, Method> ptMethodTable;

  static {
    final String PT_METHOD_PREFIX = "PT_";

    ptMethodTable = new HashMap<String, Method>();
    final Class[] classes = new Class[]{PTRowset.class, PTStandaloneRowset.class};

    for (final Class cls : classes) {
      final Method[] methods = cls.getMethods();
      for (Method m : methods) {
        if (m.getName().indexOf(PT_METHOD_PREFIX) == 0) {
          ptMethodTable.put(m.getName().substring(
              PT_METHOD_PREFIX.length()), m);
        }
      }
    }
  }

  /**
   * Remember: the provided primary record defn could be null if
   * this rowset represents the level 0 scroll of the component buffer.
   */
  public PTStandaloneRowset(final PTRowsetTypeConstraint origTc, final PTStandaloneRow pRow,
      final Record primRecDefn) {
    super(origTc);
    this.parentRow = pRow;
    this.primaryRecDefn = primRecDefn;

    // One row is always present in the rowset, even when flushed.
    this.rows.add(this.allocateNewRow());
    this.registerRecordDefn(this.primaryRecDefn);
  }

  protected PTStandaloneRow allocateNewRow() {
    return new PTRowTypeConstraint().allocStandaloneRow(
        this, this.registeredRecordDefns);
  }

  @Override
  public Callable dotMethod(final String s) {
    if (ptMethodTable.containsKey(s)) {
      return new Callable(ptMethodTable.get(s), this);
    }
    return null;
  }

  public void registerRecordDefn(final Record recDefn) {

    if (recDefn == null) {
      return;
    }

    this.registeredRecordDefns.add(recDefn);

    // Each row must also have this record registered.
    for (final PTRow row : this.rows) {
      row.registerRecordDefn(recDefn);
    }
  }

  /**
   * Fill the rowset; the WHERE clause to use must be passed on the
   * OPS runtime stack.
   */
  public void PT_Fill() {
    final List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    // If no args are provided to Fill, use a single blank as the where
    // clause.
    String whereClause = " ";
    String[] bindVals = new String[0];
    if (args.size() > 0) {
      whereClause = ((PTString) args.get(0)).read();

      // Gather bind values following the WHERE string on the stack.
      bindVals = new String[args.size() - 1];
      for (int i = 1; i < args.size(); i++) {
        final PTPrimitiveType bindExpr =
            Environment.getOrDerefPrimitive(args.get(i));
        bindVals[i - 1] = bindExpr.readAsString();
        //log.debug("Fill query bind value {}: {}", i-1, bindVals[i-1]);
      }
    }

    // The rowset must be flushed before continuing.
    this.internalFlush();

    final OPSStmt ostmt = StmtLibrary.prepareFillStmt(
        this.primaryRecDefn, whereClause, bindVals);
    OPSResultSet rs = ostmt.executeQuery();

    final List<RecordField> rfList = this.primaryRecDefn.getExpandedFieldList();
    final int numCols = rs.getColumnCount();

    if (numCols != rfList.size()) {
      throw new OPSVMachRuntimeException("The number of columns returned "
          + "by the fill query (" + numCols + ") differs from the number "
          + "of fields (" + rfList.size()
          + ") in the record defn field list.");
    }

    int rowsRead = 0;
    while (rs.next()) {

      //If at least one row exists, remove the empty row.
      if (rowsRead == 0) {
        this.rows.clear();
      }

      final PTStandaloneRow newRow = this.allocateNewRow();
      rs.readIntoRecord(newRow.getRecord(this.primaryRecDefn.RECNAME));
      this.rows.add(newRow);
      rowsRead++;
    }

    rs.close();
    ostmt.close();

    // Return the number of rows read from the fill operation.
    Environment.pushToCallStack(new PTInteger(rowsRead));
  }

  @Override
  public String toString() {
    return "[STANDALONE]" + super.toString();
  }
}
