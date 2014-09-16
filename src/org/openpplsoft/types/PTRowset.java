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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.sql.*;

/**
 * Represents a PeopleTools rowset object.
 */
public final class PTRowset extends PTObjectType {

  private static Logger log = LogManager.getLogger(
      PTRowset.class.getName());

  private static Map<String, Method> ptMethodTable;

  private List<PTRow> rows;
  private Record primaryRecDefn;
  private Set<Record> registeredRecordDefns;

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

  /**
   * Create a new Rowset object that's attached to a specific
   * record definition; can only be called by internal methods.
   * @param r the specific record defn to attach to the rowset
   */
  public PTRowset(PTRowsetTypeConstraint origTc, final Record r) {
    super(origTc);

    this.primaryRecDefn = r;
    this.rows = new ArrayList<PTRow>();
    this.registeredRecordDefns = new HashSet<Record>();

    /*
     * One row is always present in the rowset, even when flushed.
     * Note that the given record defn could be null if this rowset contains
     * the level 0 records for a level 0 scroll buffer in a component.
     */
    if (this.primaryRecDefn != null) {
      this.registeredRecordDefns.add(r);
    }
    this.rows.add(new PTRowTypeConstraint().alloc(this.registeredRecordDefns));
  }

  /**
   * Each row in a rowset contains the same underlying record
   * definitions; this method alters existing rows to ensure
   * that each has an allocated PTRecord for the newly registered
   * record defn.
   */
  public void registerRecordDefn(final Record r) {
    this.registeredRecordDefns.add(r);
    for (PTRow row : this.rows) {
      row.registerRecordDefn(r);
    }
  }

  @Override
  public PTType dotProperty(final String s) {
    if (s.equals("ActiveRowCount")) {
      return new PTInteger(this.rows.size());
    }
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
   * Get a row from the rowset; the index to use must be placed
   * on the OPS runtime stack.
   */
  public void PT_GetRow() {
    final List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if (args.size() != 1) {
      throw new OPSVMachRuntimeException("Expected only one arg.");
    }

    int idx = -1;
    final PTType iterExpr = args.get(0);

    if (iterExpr instanceof PTInteger) {
      idx = ((PTInteger) iterExpr).read();
    } else if (iterExpr instanceof PTNumber) {
      idx = ((PTNumber) iterExpr).readAsInteger();
    } else {
      throw new OPSVMachRuntimeException("Expected iterExpr to be"
          + " either PTInteger or PTNumber.");
    }

    if (idx < 1 || idx > this.rows.size()) {
      throw new OPSVMachRuntimeException("Index (" + idx + ") provided to "
          + "getRows is out of bounds; number of rows is "
          + this.rows.size());
    }

    Environment.pushToCallStack(this.getRow(idx));
  }

  /**
   * Return the row from the rowset corresponding to the given index;
   * PeopleSoft RowSet indices start from 1, not 0.
   * @param idx the index of the row to retrieve
   * @return the row corresponding to idx
   */
  public PTRow getRow(int idx) {
    // Must subtract 1 from idx; rowset indices start at 1.
    return this.rows.get(idx - 1);
  }

  /**
   * Sort the rows in the rowset; the exact order ("A" for ascending,
   * "D" for descending) must be passed on the OPS runtime stack.
   * TODO(mquinn): Technically, rows can have multiple child records
   * and 0 to n child rowsets. Keep this in mind in the event of
   * future issues with this method, as I am deferring support for those
   * scenarios.
   */
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
      if (fld != null && !fld.RECNAME.equals(this.primaryRecDefn.RECNAME)) {
        throw new OPSVMachRuntimeException("Encountered a sort "
            + "field for a record other than the one underlying this "
            + "rowset; this is legal but not yet supported.");
      }

      if (!this.primaryRecDefn.fieldTable.containsKey(fld.FIELDNAME)) {
        throw new OPSVMachRuntimeException("The field "
            + fld.FIELDNAME + " does not exist on the underlying "
            + "record.");
      }
      fields.push(fld.FIELDNAME);

    } while (!(Environment.peekAtCallStack() instanceof PTCallFrameBoundary));

    this.rows = this.mergeSortRows(this.rows, fields, orders);

    /*int i=1;
    log.debug("=========== Sorted Rowset ===========");
    for(PTRow row : this.rows) {
      PTRecord rec = row.record;
      log.debug("{}: STRM={}, ACAD_CAREER={}, INSTITUTION={}",
        i++, rec.fields.get("STRM"), rec.fields.get("ACAD_CAREER"),
        rec.fields.get("INSTITUTION"));
    }
    log.debug("======== End Sorted Rowset =========");*/
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

        final PTPrimitiveType lVal = lRow.getRecord(this.primaryRecDefn.RECNAME)
            .getFieldRef(sortFields.get(i)).deref().getValue();
        final PTPrimitiveType rVal = rRow.getRecord(this.primaryRecDefn.RECNAME)
            .getFieldRef(sortFields.get(i)).deref().getValue();

        if (lVal.isLessThan(rVal).read()) {
          if (order.equals("A")) {
            merged.add(lRow);
            l++;
          } else {
            merged.add(rRow);
            r++;
          }
          break;
        } else if (lVal.isGreaterThan(rVal).read()) {
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

  /**
   * Flush the rowset (only a default empty row will be present
   * after the flush).
   */
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
    this.rows.add(new PTRowTypeConstraint().alloc(this.registeredRecordDefns));
  }

  /**
   * Fill the rowset; the WHERE clause to use must be passed on the
   * OPS runtime stack.
   */
  public void PT_Fill() {
    final List<PTType> args = Environment.getDereferencedArgsFromCallStack();
    if (args.size() < 1) {
      throw new OPSVMachRuntimeException(
          "Expected at least one string arg.");
    }

    // Gather bind values following the WHERE string on the stack.
    final String[] bindVals = new String[args.size() - 1];
    for (int i = 1; i < args.size(); i++) {
      bindVals[i - 1] = ((PTPrimitiveType) args.get(i)).readAsString();
      //log.debug("Fill query bind value {}: {}", i-1, bindVals[i-1]);
    }

    // The rowset must be flushed before continuing.
    this.internalFlush();

    final OPSStmt ostmt = StmtLibrary.prepareFillStmt(this.primaryRecDefn,
        ((PTString) args.get(0)).read(), bindVals);
    ResultSet rs = null;

    try {
      final List<RecordField> rfList = this.primaryRecDefn.getExpandedFieldList();
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

        final PTRow newRow = new PTRowTypeConstraint().alloc(this.registeredRecordDefns);
        GlobalFnLibrary
            .readRecordFromResultSet(
            this.primaryRecDefn,
            newRow.getRecord(this.primaryRecDefn.RECNAME),
            rs);
        this.rows.add(newRow);
        rowsRead++;
      }

      // Return the number of rows read from the fill operation.
      Environment.pushToCallStack(new PTInteger(rowsRead));

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

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    if(this.primaryRecDefn == null) {
      b.append("!(CBUFFER-SCROLL-LEVEL-0-ROWSET)!");
    }
    b.append(":primaryRecDefn=").append(this.primaryRecDefn);
    b.append(",numRows=").append(this.rows.size());
    b.append(",registeredRecordDefns=").append(this.registeredRecordDefns);
    return b.toString();
  }
}
