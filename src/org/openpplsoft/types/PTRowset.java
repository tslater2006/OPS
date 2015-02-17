/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.PeopleToolsImplementation;
import org.openpplsoft.pt.Record;
import org.openpplsoft.runtime.Environment;
import org.openpplsoft.runtime.OPSVMachRuntimeException;

/**
 * Represents a PeopleTools rowset object.
 */
public abstract class PTRowset<R extends PTRow> extends PTObjectType {

  private static Logger log = LogManager.getLogger(
      PTRowset.class.getName());

  protected final List<R> rows;

  protected R parentRow;
  protected Record primaryRecDefn;

  public PTRowset(final PTRowsetTypeConstraint origTc) {
    super(origTc);
    this.rows = new ArrayList<>();
  }

  protected abstract R allocateNewRow();

  public R getParentRow() {
    return this.parentRow;
  }

  public Record getPrimaryRecDefn() {
    return this.primaryRecDefn;
  }

  public int getActiveRowCount() {
    return this.rows.size();
  }

  protected void internalFlush() {
    // One row is always present in the rowset, even when flushed.
    this.rows.clear();
    this.rows.add(this.allocateNewRow());
  }

  /**
   * Return the row from the rowset corresponding to the given index;
   * PeopleSoft RowSet indices start from 1, not 0.
   * @param idx the index of the row to retrieve
   * @return the row corresponding to idx
   */
  public R getRow(int idx) {
    // Must subtract 1 from idx; rowset indices start at 1.
    return this.rows.get(idx - 1);
  }

  @Override
  public PTType dotProperty(final String s) {
    if (s.toLowerCase().equals("activerowcount")) {
      return new PTInteger(this.getActiveRowCount());
    } else if (s.toLowerCase().equals("dbrecordname")) {
      return new PTString(this.primaryRecDefn.getRecName());
    }

    return null;
  }

  /**
   * Flush the rowset (only a default empty row will be present
   * after the flush).
   */
  @PeopleToolsImplementation
  public void Flush() {
    final List<PTType> args = Environment.getArgsFromCallStack();
    if (args.size() != 0) {
      throw new OPSVMachRuntimeException("Expected zero arguments.");
    }
    this.internalFlush();
  }

  /**
   * Get a row from the rowset; the index to use must be placed
   * on the OPS runtime stack.
   */
  @PeopleToolsImplementation
  public void GetRow() {
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
   * Sort the rows in the rowset; the exact order ("A" for ascending,
   * "D" for descending) must be passed on the OPS runtime stack.
   * TODO(mquinn): Technically, rows can have multiple child records
   * and 0 to n child rowsets. Keep this in mind in the event of
   * future issues with this method, as I am deferring support for those
   * scenarios.
   */
  @PeopleToolsImplementation
  public void Sort() {

    final LinkedList<String> sortFields = new LinkedList<String>();
    final LinkedList<String> sortOrders = new LinkedList<String>();

    do {
      final PTString orderStr = (PTString) Environment.popFromCallStack();
      if (orderStr.read().equals("A") || orderStr.read().equals("D")){
        sortOrders.push(orderStr.read());
      } else {
        throw new OPSVMachRuntimeException("Unexpected order "
          + "string: " + orderStr.read());
      }

      final PTRecordFieldSpecifier fldSpec =
          (PTRecordFieldSpecifier) Environment.popFromCallStack();
      if (!fldSpec.getRecName().equals(this.primaryRecDefn.getRecName())) {
        throw new OPSVMachRuntimeException("Encountered a sort "
            + "field for a record other than the one underlying this "
            + "rowset; this is legal but not yet supported.");
      }

      if (!this.primaryRecDefn.getFieldTable()
          .containsKey(fldSpec.getFieldName())) {
        throw new OPSVMachRuntimeException("The field "
            + fldSpec.getFieldName() + " does not exist on the underlying "
            + "record.");
      }
      sortFields.push(fldSpec.getFieldName());

    } while (!(Environment.peekAtCallStack() instanceof PTCallFrameBoundary));

    Collections.sort(this.rows, (aRow, bRow) -> {
      /*
       * Order the rows based on the precedence
       * specified in the list of sort fields,
       * along with the order accompanying each ("A" or "D").
       */
      for (int i = 0; i < sortFields.size(); i++) {
        final String order = sortOrders.get(i);

        final PTRecord<?,?> aRecord = aRow.getRecord(primaryRecDefn.getRecName());
        final PTReference<? extends PTField> aRef = aRecord
            .getFieldRef(sortFields.get(i));
        final PTPrimitiveType aVal = aRef.deref().getValue();

        final PTRecord<?,?> bRecord = bRow.getRecord(primaryRecDefn.getRecName());
        final PTReference<? extends PTField> bRef = bRecord
            .getFieldRef(sortFields.get(i));
        final PTPrimitiveType bVal = bRef.deref().getValue();

        if (aVal.isLessThan(bVal).read()) {
          if (order.equals("A")) {
            return -1;
          } else {
            return 1;
          }
        } else if (aVal.isGreaterThan(bVal).read()) {
          if (order.equals("A")) {
            return 1;
          } else {
            return -1;
          }
        } else {
          continue;
        }
      }
      return 0;
    });

    /*int i=1;
    log.debug("=========== Sorted Rowset ===========");
    for(PTRow row : this.rows) {
      PTRecord<?,?> rec = row.getRecord(1);
      log.debug("{}: STRM={}, ACAD_CAREER={}, INSTITUTION={}",
        i++, rec.getFieldRef("STRM").deref().getValue(),
        rec.getFieldRef("ACAD_CAREER").deref().getValue(),
        rec.getFieldRef("INSTITUTION").deref().getValue());
    }
    log.debug("======== End Sorted Rowset =========");*/
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(super.toString());
    b.append(":primaryRecDefn=").append(this.primaryRecDefn);
    b.append(",numRows=").append(this.rows.size());
    return b.toString();
  }
}

