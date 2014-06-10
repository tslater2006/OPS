/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package com.enterrupt.types;

import com.enterrupt.pt.*;
import java.util.*;
import java.sql.*;
import com.enterrupt.sql.*;
import com.enterrupt.runtime.*;
import java.lang.reflect.*;
import org.apache.logging.log4j.*;

public class PTRowset extends PTObjectType {

  private static Type staticTypeFlag = Type.ROWSET;
  public Record recDefn;
  private PTRow emptyRow;
  private List<PTRow> rows;
  private static Map<String, Method> ptMethodTable;

  private static Logger log = LogManager.getLogger(PTRowset.class.getName());

  static {
    // cache pointers to PeopleTools Rowset methods.
    Method[] methods = PTRowset.class.getMethods();
      ptMethodTable = new HashMap<String, Method>();
    for(Method m : methods) {
      if(m.getName().indexOf("PT_") == 0) {
        ptMethodTable.put(m.getName().substring(3), m);
      }
    }
  }

  protected PTRowset() {
    super(staticTypeFlag);
  }

  protected PTRowset(Record r) {
    super(staticTypeFlag);

    this.recDefn = r;
    this.rows = new ArrayList<PTRow>();

    // One row is always present in the rowset, even when flushed.
    this.emptyRow = new PTRow(PTRecord.getSentinel().alloc(this.recDefn));
    this.emptyRow.setReadOnly();
    this.rows.add(this.emptyRow);
  }

  public PTType dotProperty(String s) {
    if(s.equals("ActiveRowCount")) {
      return Environment.getFromLiteralPool(this.rows.size());
    }
    return null;
  }

  public Callable dotMethod(String s) {
    if(ptMethodTable.containsKey(s)) {
      return new Callable(ptMethodTable.get(s), this);
    }
    return null;
  }

  public void getRow() {
    List<PTType> args = Environment.getArgsFromCallStack();
    if(args.size() != 1) {
      throw new EntVMachRuntimeException("Expected only one arg.");
    }

    int idx = -1;
    PTType iterExpr = args.get(0);

    if(iterExpr instanceof PTInteger) {
      idx = ((PTInteger)iterExpr).read();
    } else if(iterExpr instanceof PTNumber) {
      idx = ((PTNumber)iterExpr).read(PTInteger.getSentinel());
    } else {
      throw new EntVMachRuntimeException("Expected iterExpr to be" +
        " either integer or Number.");
    }

    if(idx < 1 || idx > this.rows.size()) {
      throw new EntVMachRuntimeException("Index (" + idx + ") provided to " +
          "getRows is out of bounds; number of rows is " + this.rows.size());
    }

    // Must subtract 1 from idx; rowset indices start at 1.
    Environment.pushToCallStack(this.rows.get(idx - 1));
  }

  public void PT_Sort() {

    LinkedList<String> fields = new LinkedList<String>();
    LinkedList<String> orders = new LinkedList<String>();

    do {
      PTString orderStr = (PTString)Environment.popFromCallStack();
      if(orderStr.read().equals("A") || orderStr.read().equals("D")){
        orders.push(orderStr.read());
      } else {
        throw new EntVMachRuntimeException("Unexpected order "+
          "string: " + orderStr.read());
      }

      PTFieldLiteral fld = (PTFieldLiteral)Environment.popFromCallStack();
      if(fld != null && !fld.RECNAME.equals(this.recDefn.RECNAME)) {
        throw new EntVMachRuntimeException("Encountered a sort " +
          "field for a record other than the one underlying this "+
          "rowset; this is legal but not yet supported.");
      }

      if(!this.emptyRow.record.hasField(fld.FIELDNAME)) {
        throw new EntVMachRuntimeException("The field " +
          fld.FIELDNAME + " does not exist on the underlying "+
          "record.");
      }
      fields.push(fld.FIELDNAME);

    } while(Environment.peekAtCallStack() != null);

    this.rows = mergeSortRows(this.rows, fields, orders);

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

  private List<PTRow> mergeSortRows(List<PTRow> rowsToSort,
    List<String> sortFields, List<String> sortOrders) {

    if(rowsToSort.size() < 2) {
      return rowsToSort;
    }

    int mid = rowsToSort.size() / 2;
    List<PTRow> left = mergeSortRows(
      rowsToSort.subList(0, mid), sortFields, sortOrders);
    List<PTRow> right = mergeSortRows(
      rowsToSort.subList(mid,
        rowsToSort.size()), sortFields, sortOrders);
    return merge(left, right, sortFields, sortOrders);
  }

  private List<PTRow> merge(List<PTRow> left, List<PTRow> right,
    List<String> sortFields, List<String> sortOrders) {

    List<PTRow> merged = new ArrayList<PTRow>();
    int l = 0, r = 0;
    while(l < left.size() && r < right.size()) {
      PTRow lRow = left.get(l);
      PTRow rRow = right.get(r);

      /*
       * Order the rows based on the precedence
       * specified in the list of sort fields,
       * along with the order accompanying each (A or D).
       */
      for(int i=0; i < sortFields.size(); i++) {
        String order = sortOrders.get(i);

        PTPrimitiveType lVal = lRow.record
          .getField(sortFields.get(i)).getValue();
        PTPrimitiveType rVal = rRow.record
          .getField(sortFields.get(i)).getValue();

        if(lVal.isLessThan(rVal) == Environment.TRUE) {
          if(order.equals("A")) {
            merged.add(lRow);
            l++;
          } else {
            merged.add(rRow);
            r++;
          }
          break;
        } else if(lVal.isGreaterThan(rVal) == Environment.TRUE) {
          if(order.equals("A")) {
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
          if((i+1) == sortFields.size()) {
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
    while(l < left.size()) {
      merged.add(left.get(l++));
    }
    while(r < right.size()) {
      merged.add(right.get(r++));
    }

    return merged;
  }

  public void PT_Flush() {
    List<PTType> args = Environment.getArgsFromCallStack();
    if(args.size() != 0) {
      throw new EntVMachRuntimeException("Expected zero arguments.");
    }
    this.internalFlush();
  }

  private void internalFlush() {
    // One row is always present in the rowset, even when flushed.
    this.rows.clear();
    this.rows.add(this.emptyRow);
  }

  public void PT_Fill() {
    List<PTType> args = Environment.getArgsFromCallStack();
    if(args.size() < 1) {
      throw new EntVMachRuntimeException("Expected at least one string arg.");
    }

    // Gather bind values following the WHERE string on the stack.
    String[] bindVals = new String[args.size() - 1];
    for(int i = 1; i < args.size(); i++) {
      bindVals[i-1] = (String)((PTPrimitiveType)args.get(i)).read();
      //log.debug("Fill query bind value {}: {}", i-1, bindVals[i-1]);
    }

    // The rowset must be flushed before continuing.
    this.internalFlush();

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      List<RecordField> rfList = this.recDefn.getExpandedFieldList();

      pstmt = StmtLibrary.prepareFillStmt(this.recDefn,
          ((PTString)args.get(0)).read(), bindVals);
      rs = pstmt.executeQuery();

      int numCols = rs.getMetaData().getColumnCount();
      if(numCols != rfList.size()) {
        throw new EntVMachRuntimeException("The number of columns returned " +
          "by the fill query (" + numCols + ") differs from the number " +
          "of fields (" + rfList.size() +
          ") in the record defn field list.");
      }

      int rowsRead = 0;
      while(rs.next()) {

        //If at least one row exists, remove the empty row.
        if(rowsRead == 0) {
          this.rows.clear();
        }

        PTRecord newRecord = PTRecord.getSentinel().alloc(this.recDefn);
        GlobalFnLibrary
          .readRecordFromResultSet(this.recDefn, newRecord, rs);
        this.rows.add(new PTRow(newRecord));
        rowsRead++;
      }

      // Return the number of rows read from the fill operation.
      Environment.pushToCallStack(Environment.getFromLiteralPool(rowsRead));

    } catch(java.sql.SQLException sqle) {
      log.fatal(sqle.getMessage(), sqle);
      System.exit(ExitCode.GENERIC_SQL_EXCEPTION.getCode());
    } finally {
      try {
        if(rs != null) { rs.close(); }
        if(pstmt != null) { pstmt.close(); }
      } catch(java.sql.SQLException sqle) {}
    }
  }

  public PTPrimitiveType castTo(PTPrimitiveType t) {
    throw new EntDataTypeException("castTo() has not been implemented.");
  }

  public boolean typeCheck(PTType a) {
    return (a instanceof PTRowset &&
      this.getType() == a.getType());
  }

  public static PTRowset getSentinel() {

    // If the sentinel has already been cached, return it immediately.
    String cacheKey = getCacheKey();
    if(PTType.isSentinelCached(cacheKey)) {
      return (PTRowset)PTType.getCachedSentinel(cacheKey);
    }

    // Otherwise, create a new sentinel type and cache it before returning it.
    PTRowset sentinelObj = new PTRowset();
    PTType.cacheSentinel(sentinelObj, cacheKey);
    return sentinelObj;
  }

  /*
   * Allocated rowsets must have an associated record defn in order
   * to determine the type of the value enclosed within them. However, this
   * defn is not part of the type itself; a Rowset variable can be assigned
   * any Rowset object, regardless of its underlying record defn.
   */
  public PTRowset alloc(Record recDefn) {
    PTRowset newObj = new PTRowset(recDefn);
    PTType.clone(this, newObj);
    return newObj;
  }

  private static String getCacheKey() {
    StringBuilder b = new StringBuilder(staticTypeFlag.name());
    return b.toString();
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder(super.toString());
    b.append(":").append(recDefn.RECNAME);
    b.append(",rows=").append(rows.size());
    return b.toString();
  }
}
