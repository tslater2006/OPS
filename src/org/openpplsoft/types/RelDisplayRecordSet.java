/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.Map;
import java.util.HashMap;

import org.openpplsoft.buffers.*;
import org.openpplsoft.runtime.*;
import org.openpplsoft.types.PTRecord;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This class does not represent a PeopleTools data type;
 * it is used internally by OPS Rowsets to encapsulate the logic
 * required for representing
 * multiple related display record definitions in a single set.
 */
public class RelDisplayRecordSet {

  private static Logger log =
      LogManager.getLogger(RelDisplayRecordSet.class.getName());

  // {<DISP_CTRL_RECNAME>.<DISP_CTRL_FLDNAME> : {<RELDISP_RECNAME> : RecordEntry}}
  private Map<String, Map<String, RecordEntry>> tables;

  private class RecordEntry {
    public RecordBuffer rbuf;
    public PTBufferRecord rec;

    public RecordEntry() {}

    /**
     * Copy constructor.
     */
    public RecordEntry(final RecordEntry srcEntry) {
      this.rbuf = srcEntry.rbuf;
    }
  }

  public RelDisplayRecordSet() {
    this.tables = new HashMap<String, Map<String, RecordEntry>>();
  }

  public void registerRecord(final String dispCtrlRecFldName,
      final RecordBuffer relDispRecBuffer) {

    Map<String, RecordEntry> table = tables.get(dispCtrlRecFldName);
    if (table == null) {
      table = new HashMap<String, RecordEntry>();
      tables.put(dispCtrlRecFldName, table);
    }

    RecordEntry entry = table.get(
        relDispRecBuffer.getRecDefn().RECNAME);
    if (entry == null) {
      entry = new RecordEntry();
      entry.rbuf = relDispRecBuffer;
      table.put(relDispRecBuffer.getRecDefn().RECNAME, entry);
    }
  }

  public RelDisplayRecordSet getAllocatedCopy(final PTBufferRow parentRow) {
    final RelDisplayRecordSet allocCopy = new RelDisplayRecordSet();

    for (final Map.Entry<String, Map<String, RecordEntry>> tableEntry
        : this.tables.entrySet()) {
      final String dispCtrlRecFldName = tableEntry.getKey();
      final Map<String, RecordEntry> newTable = new HashMap<>();

      for (final Map.Entry<String, RecordEntry> entry
          : tableEntry.getValue().entrySet()) {
        final String recName = entry.getKey();
        final RecordEntry recEntry = entry.getValue();

        /*
         * Copy existing entry and allocate new PTBufferRecord within it,
         * then save in new table.
         */
        final RecordEntry copyRecEntry = new RecordEntry(recEntry);
        copyRecEntry.rec = new PTRecordTypeConstraint().allocBufferRecord(
            parentRow, copyRecEntry.rbuf);
        newTable.put(recName, copyRecEntry);
      }

      // Add the new table to the copy record set's table.
      allocCopy.tables.put(dispCtrlRecFldName, newTable);
    }

    return allocCopy;
  }

  public boolean hasRecord(final String dispCtrlRecFldName, final String recName) {
    if (this.tables.containsKey(dispCtrlRecFldName)) {
      return this.tables.get(recName).containsKey(recName);
    }
    return false;
  }

  private RecordEntry getRecordEntry(
      final String dispCtrlRecFldName, final String relDispRecName) {

    if (this.tables.containsKey(dispCtrlRecFldName)) {
      if (this.tables.get(dispCtrlRecFldName).containsKey(relDispRecName)) {
        return this.tables.get(dispCtrlRecFldName).get(relDispRecName);
      }
    }

    throw new OPSVMachRuntimeException("Record entry does not exist in rel "
        + "display record set for dispCtrlRecFldName=" + dispCtrlRecFldName
        + " and relDispRecName= " + relDispRecName);
  }

  public RecordBuffer getRecordBuffer(
      final String dispCtrlRecFldName, final String relDispRecName) {
    return this.getRecordEntry(dispCtrlRecFldName, relDispRecName).rbuf;
  }

  public PTBufferRecord getRecord(
      final String dispCtrlRecFldName, final String relDispRecName) {
    return this.getRecordEntry(dispCtrlRecFldName, relDispRecName).rec;
  }
}

