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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.sql.*;

public final class PTRowset extends PTObjectType {

  private static Logger log = LogManager.getLogger(
      PTRowset.class.getName());

  private static Type staticTypeFlag = Type.ROWSET;
  private static Map<String, Method> ptMethodTable;

  private Record recDefn;
  private PTRow emptyRow;
  private List<PTRow> rows;

  static {
    final String PT_METHOD_PREFIX = "PT_";
    // cache pointers to PeopleTools Rowset methods.
    final Method[] methods = PTRowset.class.getMethods();
    ptMethodTable = new HashMap<String, Method>();
    for (Method m : methods) {
      if (m.getName().indexOf(PT_METHOD_PREFIX) == 0) {
        ptMethodTable.put(m.getName().substring(
            PT_METHOD_PREFIX.length()), m);
      }
    }
  }

  protected PTRowset() {
    super(staticTypeFlag);
  }

  protected PTRowset(final Record r) {
    super(staticTypeFlag);

    this.recDefn = r;
    this.rows = new ArrayList<PTRow>();

    // One row is always present in the rowset, even when flushed.
    this.emptyRow = new PTRow(PTRecord.getSentinel().alloc(this.recDefn));
    this.emptyRow.setReadOnly();
    this.rows.add(this.emptyRow);
  }

  public PTType dotProperty(final String s) {
    if (s.equals("ActiveRowCount")) {
      return Environment.getFromLiteralPool(this.rows.size());
    }
    return null;
  }

  public Callable dotMethod(final String s) {
    if (ptMethodTable.containsKey(s)) {
      return new Callable(ptMethodTable.get(s), this);
    }
    return null;
  }

  public void getRow() {
    final List<PTType> args = Environment.getArgsFromCallStack();
    if (args.size() != 1) {
      throw new OPSVMachRuntimeException("Expected only one arg.");
    }

    int idx = -1;
    final PTType iterExpr = args.get(0);

    if (iterExpr instanceof PTInteger) {
      idx = ((PTInteger) iterExpr).read();
    } else if (iterExpr instanceof PTNumber) {
      idx = ((PTNumber) iterExpr).read(PTInteger.getSentinel());
    } else {
      throw new OPSVMachRuntimeException("Expected iterExpr to be"
          + " either integer or Number.");
    }

    if (idx < 1 || idx > this.rows.size()) {
      throw new OPSVMachRuntimeException("Index (" + idx + ") provided to "
          + "getRows is out of bounds; number of rows is "
          + this.rows.size());
    }

    // Must subtract 1 from idx; rowset indices start at 1.
    Environment.pushToCallStack(this.rows.get(idx - 1));
  }

  public void PT_Sort() {

    final LinkedList<String> fields = new LinkedList<String>();
    final LinkedList<String> orders = new LinkedList<String>();

    do {
      final PTString orderStr = (PTString) Environment.popFromCallStack();
      if (orderStr.read().equals("A") || orderStr.read().equals("D")){
        orders.push(orderStr.read());
      } else {
        throw new OPSVMachRuntimeException("Unexpected order "
          + "string: " + orderStr.read());
      }

      final PTFieldLiteral fld =
          (PTFieldLiteral) Environment.popFromCallStack();
      if (fld != null && !fld.RECNAME.equals(this.recDefn.RECNAME)) {
        throw new OPSVMachRuntimeException("Encountered a sort "
            + "field for a record other than the one underlying this "
            + "rowset; this is legal but not yet supported.");
      }

      if (!this.emptyRow.record.hasField(fld.FIELDNAME)) {
        throw new OPSVMachRuntimeException("The field "
            + fld.FIELDNAME + " does not exist on the underlying "
            + "record.");
      }
      fields.push(fld.FIELDNAME);

    } while (Environment.peekAtCallStack() != null);

    this.rows = this.mergeSortRows(this.rows, fields, orders);

    /*
    int i=1;
    log.debug("=========== Sorted Rowset ===========");
    for(PTRow row : this.rows) {
      PTRecord rec = row.record;
      log.debug("{}: STRM={}, ACAD_CAREER={}, INSTITUTION={}",
        i++, rec.fields.get("STRM"), rec.fields.get("ACAD_CAREER"),
        rec.fields.get("INSTITUTION"));
    }
    log.debug("======== End Sorted Rowset =========");
    */
  }

  private List<PTRow> mergeSortRows(final List<PTRow> rowsToSort,
    final List<String> sortFields, final List<String> sortOrders) {

    if (rowsToSort.size() < 2) {
      return rowsToSort;
    }

    final int mid = rowsToSort.size() / 2;
    final List<PTRow> left = this.mergeSortRows(
        rowsToSort.subList(0, mid), sortFields, sortOrders);
    final List<PTRow> right = this.mergeSortRows(
        rowsToSort.subList(mid,
          rowsToSort.size()), sortFields, sortOrders);
    return this.merge(left, right, sortFields, sortOrders);
  }

  private List<PTRow> merge(final List<PTRow> left,
      final List<PTRow> right, final List<String> sortFields,
      final List<String> sortOrders) {

    final List<PTRow> merged = new ArrayList<PTRow>();
    int l = 0, r = 0;
    while (l < left.size() && r < right.size()) {
      final PTRow lRow = left.get(l);
      final PTRow rRow = right.get(r);

      /*
       * Order the rows based on the precedence
       * specified in the list of sort fields,
       * along with the order accompanying each (A or D).
       */
      for (int i = 0; i < sortFields.size(); i++) {
        final String order = sortOrders.get(i);

        final PTPrimitiveType lVal = lRow.record
            .getField(sortFields.get(i)).getValue();
        final PTPrimitiveType rVal = rRow.record
            .getField(sortFields.get(i)).getValue();

        if (lVal.isLessThan(rVal) == Environment.TRUE) {
          if (order.equals("A")) {
            merged.add(lRow);
            l++;
          } else {
            merged.add(rRow);
            r++;
          }
          break;
        } else if (lVal.isGreaterThan(rVal) == Environment.TRUE) {
          if (order.equals("A")) {
            merged.add(rRow);
            r++;
          } else {
            merged.add(lRow);
            l++;
          }
          break;
        } else {
          /*
           * If this is the last sort field,
           * and the rows are still considered "equal"
           * in terms of ordering, add both to the merged
           * array in terms of their natural ordering.
           */
          if ((i + 1) == sortFields.size()) {
            merged.add(lRow);
            l++;
            merged.add(rRow);
            r++;
          } else {
            /*
             * If another sort field exists,
             * use that to determine ordering.
             */
            continue;
          }
        }
      }
    }

    /*
     * Add any surplus elements to end of merged array.
     */
    while (l < left.size()) {
      merged.add(left.get(l++));
    }
    while (r < right.size()) {
      merged.add(right.get(r++));
    }

    return merged;
  }

  public void PT_Flush() {
    final List<PTType> args = Environment.getArgsFromCallStack();
    if (args.size() != 0) {
      throw new OPSVMachRuntimeException("Expected zero arguments.");
    }
    this.internalFlush();
  }

  private void internalFlush() {
    // One row is always present in the rowset, even when flushed.
    this.rows.clear();
    this.rows.add(this.emptyRow);
  }

  public void PT_Fill() {
    final List<PTType> args = Environment.getArgsFromCallStack();
    if (args.size() < 1) {
      throw new OPSVMachRuntimeException(
          "Expected at least one string arg.");
    }

    // Gather bind values following the WHERE string on the stack.
    final String[] bindVals = new String[args.size() - 1];
    for (int i = 1; i < args.size(); i++) {
      bindVals[i - 1] = (String) ((PTPrimitiveType) args.get(i)).read();
      //log.debug("Fill query bind value {}: {}", i-1, bindVals[i-1]);
    }

    // The rowset must be flushed before continuing.
    this.internalFlush();

    final OPSStmt ostmt = StmtLibrary.prepareFillStmt(this.recDefn,
        ((PTString) args.get(0)).read(), bindVals);
    ResultSet rs = null;

    try {
      final List<RecordField> rfList = this.recDefn.getExpandedFieldList();
      rs = ostmt.executeQuery();

      final int numCols = rs.getMetaData().getColumnCount();
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

        final PTRecord newRecord = PTRecord.getSentinel().alloc(this.recDefn);
        GlobalFnLibrary
            .readRecordFromResultSet(this.recDefn, newRecord, rs);
        this.rows.add(new PTRow(newRecord));
        rowsRead++;
      }

      // Return the number of rows read from the fill operation.
      Environment.pushToCallStack(Environment.getFromLiteralPool(rowsRead));

    } catch (final java.sql.SQLException sqle) {
      log.fatal(sqle.getMessage(), sqle);
      System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
    } finally {
      try {
        if (rs != null) { rs.close(); }
        if (ostmt != null) { ostmt.close(); }
      } catch (final java.sql.SQLException sqle) {
        log.warn("Unable to close ostmt and/or rs in finally block.");
      }
    }
  }

  public PTPrimitiveType castTo(final PTPrimitiveType t) {
    throw new EntDataTypeException("castTo() has not been implemented.");
  }

  public boolean typeCheck(final PTType a) {
    return (a instanceof PTRowset
        && this.getType() == a.getType());
  }

  public static PTRowset getSentinel() {

    // If the sentinel has already been cached, return it immediately.
    final String cacheKey = getCacheKey();
    if (PTType.isSentinelCached(cacheKey)) {
      return (PTRowset) PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    final PTRowset sentinelObj = new PTRowset();
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  /*
   * Allocated rowsets must have an associated record defn in order
   * to determine the type of the value enclosed within them. However, this
   * defn is not part of the type itself; a Rowset variable can be assigned
   * any Rowset object, regardless of its underlying record defn.
   */
  public PTRowset alloc(final Record r) {
    final PTRowset newObj = new PTRowset(r);
    PTType.clone(this, newObj);
    return newObj;
  }

  private static String getCacheKey() {
    final StringBuilder b = new StringBuilder(staticTypeFlag.name());
    return b.toString();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(":").append(this.recDefn.RECNAME);
    b.append(",rows=").append(this.rows.size());
    return b.toString();
  }
}
