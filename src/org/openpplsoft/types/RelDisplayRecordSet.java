/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package org.openpplsoft.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.openpplsoft.buffers.*;
import org.openpplsoft.pt.*;
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
  private final Map<String, Map<String, RecordEntry>> tables;

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
        relDispRecBuffer.getRecDefn().getRecName());
    if (entry == null) {
      entry = new RecordEntry();
      entry.rbuf = relDispRecBuffer;
      table.put(relDispRecBuffer.getRecDefn().getRecName(), entry);
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

        // The parent's totally ordered record map will be incomplete without
        // telling it about all allocated reldisp records in the RelDisplayRecordSet
        // that will be returned to it.
        parentRow.addRelDispRecordToTotallyOrderedMap(
            copyRecEntry.rbuf.getOrderIdx(), copyRecEntry.rec);
      }

      // Add the new table to the copy record set's table.
      allocCopy.tables.put(dispCtrlRecFldName, newTable);
    }

    return allocCopy;
  }

  public boolean hasRecord(
      final String dispCtrlRecFldName, final String relDispRecName) {
    if (this.tables.containsKey(dispCtrlRecFldName)) {
      return this.tables.get(dispCtrlRecFldName).containsKey(relDispRecName);
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

  public List<PTBufferRecord> getAllRecordsNamed(final String recName) {
    final List<PTBufferRecord> records = new ArrayList<>();
    for (final Map.Entry<String, Map<String, RecordEntry>> tableEntry
        : this.tables.entrySet()) {
      for (final Map.Entry<String, RecordEntry> entry
          : tableEntry.getValue().entrySet()) {
        if (entry.getValue().rec.getRecDefn().getRecName().equals(recName)) {
          records.add(entry.getValue().rec);
        }
      }
    }
    return records;
  }

  /**
   * This method returns the record count, as expected during tracefile
   * verification. Do not use this method for any purpose other than
   * tracefile verification.
   */
  public int getEmissionRecordCount() {
    int total = 0;
    for (final Map.Entry<String, Map<String, RecordEntry>> tableEntry
        : this.tables.entrySet()) {
      for (final Map.Entry<String, RecordEntry> entry
          : tableEntry.getValue().entrySet()) {
        if (!PSDefn.isSystemRecord(entry.getValue().rec.getRecDefn().getRecName())) {
          total++;
        }
      }
    }
    return total;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("RelDisplayRecordSet tables:\n");
    for (final Map.Entry<String, Map<String, RecordEntry>> tableEntry
        : this.tables.entrySet()) {
      sb.append("- dispctrl field:")
          .append(tableEntry.getKey()).append(": table{\n");
      for (final Map.Entry<String, RecordEntry> entry
          : tableEntry.getValue().entrySet()) {
        sb.append("    -> ").append(entry.getKey()).append(": record{\n");
        final PTBufferRecord rec = entry.getValue().rec;
        for (final RecordFieldBuffer rfbuf
            : rec.getRecBuffer().getFieldBuffers()) {
          sb.append("        ").append(rfbuf).append("\n");
        }
/*        for (final PTImmutableReference<PTBufferField> fldref
            : rec.getFieldRefsInAlphabeticOrderByFieldName()) {
          sb.append("       ").append(fldref.deref().getRecordFieldBuffer())
              .append("\n");
        }*/
        sb.append("      }\n");
      }
      sb.append("}\n");
    }
    return sb.toString();
  }

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
}
