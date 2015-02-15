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
 * Represents a PeopleTools rowset in the component buffer.
 */
public final class PTBufferRowset extends PTRowset<PTBufferRow>
    implements ICBufferEntity {

  private static Logger log = LogManager.getLogger(
      PTBufferRowset.class.getName());

  private static Map<String, Method> ptMethodTable;

  private ScrollBuffer scrollBuffer;

  private RelDisplayRecordSet relDisplayRecordSet = new RelDisplayRecordSet();
  private List<RecordBuffer> registeredNonRelDispRecordBuffers = new ArrayList<>();
  private List<ScrollBuffer> registeredChildScrollBuffers = new ArrayList<>();

  static {
    final String PT_METHOD_PREFIX = "PT_";

    ptMethodTable = new HashMap<String, Method>();
    final Class[] classes = new Class[]{PTRowset.class, PTBufferRowset.class};

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

  public PTBufferRowset(final PTRowsetTypeConstraint origTc, final PTBufferRow pRow,
      final ScrollBuffer scrollBuf) {
    super(origTc);
    this.parentRow = pRow;
    this.scrollBuffer = scrollBuf;
    this.primaryRecDefn = scrollBuf.getPrimaryRecDefn();

    for (final RecordBuffer recBuf : scrollBuf.getOrderedNonRelDispRecBuffers()) {
      this.registeredNonRelDispRecordBuffers.add(recBuf);
    }

    for (final ScrollBuffer childScrollBuf : scrollBuf.getOrderedScrollBuffers()) {
      this.registeredChildScrollBuffers.add(childScrollBuf);
    }

    this.relDisplayRecordSet = scrollBuf.getRelDisplayRecordSet();

    // One row is always present in the rowset, even when flushed.
    this.rows.add(this.allocateNewRow());
  }

  /**
   * The topmost rowset in the component buffer holds the search
   * record as its primary record defn. In this case, there is no parent
   * row, nor is there a scroll buffer defn for this rowset. Later, when
   * the component buffer is materialized, a child rowset (the level 0 rowset)
   * will be added dynamically.
   */
  public PTBufferRowset(final PTRowsetTypeConstraint origTc,
      final SearchRecordBuffer searchRecBuf) {
    super(origTc);
    this.primaryRecDefn = searchRecBuf.getRecDefn();
    this.registeredNonRelDispRecordBuffers.add(searchRecBuf);

    // One row is always present in the rowset, even when flushed.
    this.rows.add(this.allocateNewRow());
  }

  public ScrollBuffer getCBufferScrollBuffer() {
    return this.scrollBuffer;
  }

  public List<RecordBuffer> getRegisteredNonRelDispRecordBuffers() {
    return this.registeredNonRelDispRecordBuffers;
  }

  public List<ScrollBuffer> getRegisteredChildScrollBuffers() {
    return this.registeredChildScrollBuffers;
  }

  public RelDisplayRecordSet getRelDisplayRecordSet() {
    return this.relDisplayRecordSet;
  }

  protected PTBufferRow allocateNewRow() {
    return new PTRowTypeConstraint().allocBufferRow(this);
  }

  /**
   * This method is only intended to be used when materializing the level 0
   * row of the component buffer.
   */
  public void dynamicallyRegisterChildScrollBuffer(
      final ScrollBuffer childScrollBuf) {
    for (final PTBufferRow row : this.rows) {
      row.dynamicallyRegisterChildScrollBuffer(childScrollBuf);
    }
  }

  public void fireEvent(final PCEvent event,
      final FireEventSummary fireEventSummary) {
    for (final PTBufferRow row : this.rows) {
      row.fireEvent(event, fireEventSummary);
    }
  }

  @Override
  public Callable dotMethod(final String s) {
    if (ptMethodTable.containsKey(s)) {
      return new Callable(ptMethodTable.get(s), this);
    }
    return null;
  }

  public PTBufferRecord resolveContextualCBufferRecordReference(final String recName) {
    if (this.parentRow != null) {
      return this.parentRow.resolveContextualCBufferRecordReference(recName);
    }
    return null;
  }

  public PTReference<PTBufferField> resolveContextualCBufferRecordFieldReference(
      final String recName, final String fieldName) {
    if (this.parentRow != null) {
      return this.parentRow.resolveContextualCBufferRecordFieldReference(
          recName, fieldName);
    }
    return null;
  }

  public PTBufferRowset resolveContextualCBufferScrollReference(
      final PTScrollLiteral scrollName) {
    if (this.parentRow != null) {
      return this.parentRow.resolveContextualCBufferScrollReference(scrollName);
    }
    return null;
  }

  public void emitScrolls(final ScrollEmissionContext ctxFlag, final int indent) {

    String indentStr = "";
    for (int i = 0; i < indent - 1; i++) {
      indentStr += "|  ";
    }

    if (ctxFlag != ScrollEmissionContext.SEARCH_RESULTS
        && this.determineScrollLevel() > 0) {
      TraceFileVerifier.submitEnforcedEmission(
          new ScrollIndex(indentStr, this.scrollBuffer
              .getIndexOfThisChildScrollBufferInParent()));
    }

    for (int i = 0; i < this.rows.size(); i++) {
      this.rows.get(i).emitScrolls(ctxFlag, indent);
    }
  }

  public void runFieldDefaultProcessing(
      final FieldDefaultProcSummary fldDefProcSummary) {
    for (final PTBufferRow row : this.rows) {
      row.runFieldDefaultProcessing(fldDefProcSummary);
    }
  }

  /**
   * If a key lookup request reaches a Rowset, the request should
   * always be passed to the parent row in order to look at the child
   * records within that row.
   */
  public void generateKeylist(
      final String fieldName, final Keylist keylist) {
    if (this.parentRow != null) {
      this.parentRow.generateKeylist(fieldName, keylist);
    }
  }

  public int getIndexOfRow(final PTBufferRow row) {
    for (int i = 0; i < this.rows.size(); i++) {
      if(row == this.rows.get(i)) {
        return i;
      }
    }
    throw new OPSVMachRuntimeException("Unable to get index for the provided row; "
        + "row does not exist in this rowset.");
  }

  public int determineScrollLevel() {
    if (this == ComponentBuffer.getCBufferRowset()) {
      return -1;
    } else if (this.parentRow != null) {
      return 1 + this.parentRow.determineScrollLevel();
    } else {
      throw new OPSVMachRuntimeException("Unable to determine scroll level "
          + "for this rowset; this is not the root cbuffer rowset, but "
          + "the parent row is null, so unable to continue traversal.");
    }
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
      final PTBufferRow rowToWriteTo = this.getRow(rowIdxToWriteTo);
      final PTRecord recToWriteTo = rowToWriteTo.getRecord(this.primaryRecDefn.getRecName());

      /**
       * It is possible to select from a different record than the
       * record used as the rowset's primary record. If this is the case,
       * we need to read/write only those fields that are shared by both.
       * Otherwise, read into the record as usual.
       */
      if (!this.primaryRecDefn.getRecName().equals(recToSelectFrom.getRecName())) {
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

    TraceFileVerifier.submitEnforcedEmission(new BeginScrolls(
        ScrollEmissionContext.AFTER_SCROLL_SELECT));
    ComponentBuffer.getLevelZeroRowset().emitScrolls(
        ScrollEmissionContext.AFTER_SCROLL_SELECT, 0);
    TraceFileVerifier.submitEnforcedEmission(new EndScrolls());
  }

  @Override
  public String toString() {
    return "[BUFFER]" + super.toString();
  }
}
