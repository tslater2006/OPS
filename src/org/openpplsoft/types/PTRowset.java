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
 * Represents a PeopleTools rowset object.
 */
public abstract class PTRowset extends PTObjectType {

  private static Logger log = LogManager.getLogger(
      PTRowset.class.getName());

  private static Map<String, Method> ptMethodTable;

  protected PTRow parentRow;
  protected List<PTRow> rows = new ArrayList<PTRow>();
  protected Record primaryRecDefn;

  protected Set<Record> registeredRecordDefns = new HashSet<Record>();
  protected Map<String, ScrollBuffer> registeredChildScrollDefns =
      new LinkedHashMap<String, ScrollBuffer>();

  static {
    final String PT_METHOD_PREFIX = "PT_";

    // cache pointers to superclass PTRowset methods (applicable to
    // all rowsets regardless of whether or not they're in the comp buffer).
    Method[] methods = PTRowset.class.getMethods();
    ptMethodTable = new HashMap<String, Method>();
    for (Method m : methods) {
      if (m.getName().indexOf(PT_METHOD_PREFIX) == 0) {
        ptMethodTable.put(m.getName().substring(
            PT_METHOD_PREFIX.length()), m);
      }
    }
  }

  protected static Map<String, Method> getUniversalRowsetMethodTable() {
    return ptMethodTable;
  }

  public PTRowset(final PTRowsetTypeConstraint origTc) {
    super(origTc);
  }

  protected abstract PTRow allocateNewRow();
  public abstract void generateKeylist(String fieldName, Keylist keylist);
  public abstract void emitScrolls(String indent);
  public abstract void fireEvent(PCEvent event, FireEventSummary fireEventSummary);
  public abstract void runFieldDefaultProcessing(FieldDefaultProcSummary fldDefProcSummary);
  public abstract PTRowset resolveContextualCBufferScrollReference(PTScrollLiteral scrollName);
  public abstract PTRecord resolveContextualCBufferRecordReference(String recName);
  public abstract PTReference<PTField> resolveContextualCBufferRecordFieldReference(String recName, String fldName);
  public abstract ScrollBuffer getCBufferScrollDefn();
  public abstract int getIndexOfRow(PTRow row);
  public abstract int determineScrollLevel();
  public abstract void registerRecordDefn(Record rec);
  public abstract void registerChildScrollDefn(ScrollBuffer scrollBuf);

  public PTRow getParentRow() {
    return this.parentRow;
  }

  public int getActiveRowCount() {
    return this.rows.size();
  }

  @Override
  public PTType dotProperty(final String s) {
    if (s.toLowerCase().equals("activerowcount")) {
      return new PTInteger(this.getActiveRowCount());
    } else if (s.toLowerCase().equals("dbrecordname")) {
      return new PTString(this.primaryRecDefn.RECNAME);
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

      final PTRecordFieldSpecifier fldSpec =
          (PTRecordFieldSpecifier) Environment.popFromCallStack();
      if (!fldSpec.getRecName().equals(this.primaryRecDefn.RECNAME)) {
        throw new OPSVMachRuntimeException("Encountered a sort "
            + "field for a record other than the one underlying this "
            + "rowset; this is legal but not yet supported.");
      }

      if (!this.primaryRecDefn.fieldTable.containsKey(fldSpec.getFieldName())) {
        throw new OPSVMachRuntimeException("The field "
            + fldSpec.getFieldName() + " does not exist on the underlying "
            + "record.");
      }
      fields.push(fldSpec.getFieldName());

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

  protected void internalFlush() {
    // One row is always present in the rowset, even when flushed.
    this.rows.clear();
    this.rows.add(this.allocateNewRow());
  }
}
