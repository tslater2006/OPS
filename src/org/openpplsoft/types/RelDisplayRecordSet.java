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

    Map<String, RecordEntry> table = tables.get(
        relDispRecBuffer.getRecDefn().RECNAME);
    if (table == null) {
      table = new HashMap<String, RecordEntry>();
      tables.put(relDispRecBuffer.getRecDefn().RECNAME, table);
    }

    RecordEntry entry = table.get(dispCtrlRecFldName);
    if (entry == null) {
      entry = new RecordEntry();
      entry.rbuf = relDispRecBuffer;
      table.put(dispCtrlRecFldName, entry);
    }
  }

  public RelDisplayRecordSet getAllocatedCopy(final PTBufferRow parentRow) {
    final RelDisplayRecordSet allocCopy = new RelDisplayRecordSet();

    for (final Map.Entry<String, Map<String, RecordEntry>> tableEntry
        : this.tables.entrySet()) {
      final String recName = tableEntry.getKey();
      final Map<String, RecordEntry> newTable = new HashMap<>();

      for (final Map.Entry<String, RecordEntry> dispCtrlEntry
          : tableEntry.getValue().entrySet()) {
        final String dispCtrlRecFldName = dispCtrlEntry.getKey();
        final RecordEntry recEntry = dispCtrlEntry.getValue();

        /*
         * Copy existing entry and allocate new PTBufferRecord within it,
         * then save in new table.
         */
        final RecordEntry copyRecEntry = new RecordEntry(recEntry);
        copyRecEntry.rec = new PTRecordTypeConstraint().allocBufferRecord(
            parentRow, copyRecEntry.rbuf);
        newTable.put(dispCtrlRecFldName, copyRecEntry);
      }

      // Add the new table to the copy record set's table.
      allocCopy.tables.put(recName, newTable);
    }

    return allocCopy;
  }

  public boolean hasRecord(final String recName, final String dispCtrlRecFldName) {
    if (this.tables.containsKey(recName)) {
      return this.tables.get(recName).containsKey(dispCtrlRecFldName);
    }
    return false;
  }

  public RecordBuffer getRecordBuffer(final String recName,
      final String dispCtrlRecFldName) {
    if (this.tables.containsKey(recName)) {
      if (this.tables.get(recName).containsKey(dispCtrlRecFldName)) {
        return this.tables.get(recName).get(dispCtrlRecFldName).rbuf;
      }
    }

    throw new OPSVMachRuntimeException("Record buffer does not exist in rel "
        + "display record set for recName=" + recName + " and dispCtrlRecFldName "
        + dispCtrlRecFldName);
  }
}

