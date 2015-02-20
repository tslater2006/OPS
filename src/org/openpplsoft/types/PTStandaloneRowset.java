/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openpplsoft.pt.PeopleToolsImplementation;
import org.openpplsoft.pt.Record;
import org.openpplsoft.pt.RecordField;
import org.openpplsoft.runtime.Environment;
import org.openpplsoft.runtime.OPSVMachRuntimeException;
import org.openpplsoft.sql.OPSResultSet;
import org.openpplsoft.sql.OPSStmt;
import org.openpplsoft.sql.StmtLibrary;

/**
 * Represents a PeopleTools standalone (not in component buffer) rowset.
 */
public final class PTStandaloneRowset extends PTRowset<PTStandaloneRow> {

  private static Logger log = LogManager.getLogger(
      PTStandaloneRowset.class.getName());

  private final List<Record> registeredRecordDefns;

  /**
   * Remember: the provided primary record defn could be null if
   * this rowset represents the level 0 scroll of the component buffer.
   */
  public PTStandaloneRowset(final PTRowsetTypeConstraint origTc,
      final PTStandaloneRow pRow, final Record primRecDefn) {
    super(origTc);
    this.parentRow = pRow;
    this.primaryRecDefn = primRecDefn;
    this.registeredRecordDefns = new ArrayList<>();

    if (primRecDefn != null) {
      this.registeredRecordDefns.add(this.primaryRecDefn);
    } else {
      throw new OPSVMachRuntimeException("Encountered null primary rec defn "
          + "during call to instantiate standalone rowset.");
    }

    // Rowsets are always initialized with a dummy row.
    final PTStandaloneRow dummyRow = this.allocateNewRow();
    dummyRow.tagAsDummy();
    this.rows.add(dummyRow);
  }

  protected List<Record> getRegisteredRecordDefns() {
    return this.registeredRecordDefns;
  }

  protected PTStandaloneRow allocateNewRow() {
    return new PTRowTypeConstraint().allocStandaloneRow(this);
  }

  /**
   * Fill the rowset; the WHERE clause to use must be passed on the
   * OPS runtime stack.
   */
  @PeopleToolsImplementation
  public void Fill() {
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
      rs.readIntoRecord(newRow.getRecord(this.primaryRecDefn.getRecName()));
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
