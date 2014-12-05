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

  private void internalFlush() {
    // One row is always present in the rowset, even when flushed.
    this.rows.clear();
    this.rows.add(this.allocateNewRow());
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

      final PTRow newRow = this.allocateNewRow();
      rs.readIntoRecord(newRow.getRecord(this.primaryRecDefn.RECNAME));
      this.rows.add(newRow);
      rowsRead++;
    }

    rs.close();
    ostmt.close();

    // Return the number of rows read from the fill operation.
    Environment.pushToCallStack(new PTInteger(rowsRead));
  }

  public void PT_Select() {

    final List<PTType> args = Environment.getDereferencedArgsFromCallStack();

    if (args.size() == 0) {
      throw new OPSVMachRuntimeException("Select requires at least one arg.");
    }

    if (!(args.get(0) instanceof PTRecordLiteral)) {
      /*
       * IMPORTANT: When supporting ScrollLiterals here, remember that:
       * "The first scrollname must be a child rowset of the rowset
       *  object executing the method, the second scrollname must be a
       *  child of the first child, and so on."
       */
      throw new OPSVMachRuntimeException("Expected RecordLiteral as first arg "
          + "to Select; note that Select allows multiple (optional) ScrollLiterals to "
          + "be passed before the required RecordLiteral, so may need to support "
          + "this now.");
    }

    final Record recToSelectFrom =
        DefnCache.getRecord(((PTRecordLiteral) args.get(0)).read());
    int nextBindVarIdx = 1;

    /*
     * Note that Select does not require a WHERE string to be passed in.
     */
    String whereStr = null;
    if (args.get(1) instanceof PTString) {
      whereStr = ((PTString) args.get(1)).read();
      nextBindVarIdx++;
    }

    /*
     * If any bind vars have been passed in, accumulate them now.
     */
    final List<String> bindVals = new ArrayList<String>();
    while (nextBindVarIdx < args.size()) {
      final PTPrimitiveType primArg =
          Environment.getOrDerefPrimitive(args.get(nextBindVarIdx++));
      bindVals.add(primArg.readAsString());
    }

    log.debug("Selecting into rowset: {}", this);
    final OPSStmt ostmt = StmtLibrary.prepareSelectStmt(
        recToSelectFrom, whereStr, bindVals.toArray(new String[bindVals.size()]));
    OPSResultSet rs = ostmt.executeQuery();

    int rowsRead = 0, rowIdxToWriteTo = 1;
    while (rs.next()) {
      final PTRow rowToWriteTo = this.getRow(rowIdxToWriteTo);
      final PTRecord recToWriteTo = rowToWriteTo.getRecord(this.primaryRecDefn.RECNAME);

      /**
       * It is possible to select from a different record than the
       * record used as the rowset's primary record. If this is the case,
       * we need to read/write only those fields that are shared by both.
       * Otherwise, read into the record as usual.
       */
      if (!this.primaryRecDefn.RECNAME.equals(recToSelectFrom.RECNAME)) {
        rs.readIntoRecordDefinedFieldsOnly(recToWriteTo);
      } else {
        rs.readIntoRecord(recToWriteTo);
      }

      rowsRead++;
    }

    rs.close();
    ostmt.close();

    // Return the number of rows read from the fill operation.
    Environment.pushToCallStack(new PTInteger(rowsRead));

    TraceFileVerifier.submitEnforcedEmission(new BeginScrolls("After ScrollSelect"));
    ComponentBuffer.getLevelZeroRowset().emitScrolls("");
    TraceFileVerifier.submitEnforcedEmission(new EndScrolls());
  }
}
