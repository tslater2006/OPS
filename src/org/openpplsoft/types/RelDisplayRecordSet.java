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
    public PTRecord rec;
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

  public PTRecord registerAndAllocRecord(final String dispCtrlRecFldName,
      final RecordBuffer relDispRecBuffer) {
    return null;
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

