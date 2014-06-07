/*===---------------------------------------------------------------------===*\
|*                       The OpenPplSoft Runtime Project                     *|
|*                                                                           *|
|*              This file is distributed under the MIT License.              *|
|*                         See LICENSE.md for details.                       *|
\*===---------------------------------------------------------------------===*/

package com.enterrupt.pt;

import java.sql.*;
import java.util.*;
import com.enterrupt.sql.StmtLibrary;
import com.enterrupt.runtime.*;
import com.enterrupt.pt.peoplecode.*;
import org.apache.logging.log4j.*;

public class Record {

  public String RECNAME;
  public String RELLANGRECNAME;
  public int RECTYPE;

  public HashMap<String, RecordField> fieldTable;
  public TreeMap<Integer, Object> fldAndSubrecordTable;
  public ArrayList<String> subRecordNames;
  public HashMap<String, ArrayList<PeopleCodeProg>> recordProgsByFieldTable;
  public ArrayList<PeopleCodeProg> orderedRecordProgs;

  private static Logger log = LogManager.getLogger(Record.class.getName());

  private boolean hasRecordPCBeenDiscovered = false;
  private boolean hasBeenInitialized = false;

  public Record(String recname) {
    this.RECNAME = recname;
  }

  public void init() {

    if(this.hasBeenInitialized) { return; }
    this.hasBeenInitialized = true;

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      pstmt = StmtLibrary.getPSRECDEFN(this.RECNAME);
      rs = pstmt.executeQuery();

      int fieldcount = 0;
      if(rs.next()) {
        fieldcount = rs.getInt("FIELDCOUNT");
        this.RELLANGRECNAME = rs.getString("RELLANGRECNAME").trim();
        this.RECTYPE = rs.getInt("RECTYPE");
      } else {
        throw new EntVMachRuntimeException("Expected record to be returned from PSRECDEFN query: " + this.RECNAME);
      }
      rs.close();
      pstmt.close();

      pstmt = StmtLibrary.getPSDBFIELD_PSRECFIELD_JOIN(this.RECNAME);
      rs = pstmt.executeQuery();

      this.fieldTable = new HashMap<String, RecordField>();
      this.fldAndSubrecordTable = new TreeMap<Integer, Object>();

      int i = 0;
      while(rs.next()) {
        RecordField f = new RecordField(this.RECNAME,
            rs.getString("FIELDNAME").trim(), rs.getInt("FIELDTYPE"));
        f.USEEDIT = rs.getInt("USEEDIT");
        f.FIELDNUM = rs.getInt("FIELDNUM");
        this.fieldTable.put(f.FIELDNAME, f);
        this.fldAndSubrecordTable.put(f.FIELDNUM, f);
          i++;
        }
        rs.close();
        pstmt.close();

        this.subRecordNames = new ArrayList<String>();

        /*
         * If the number of fields retrieved differs from the FIELDCOUNT for the record
         * definition, we need to query for subrecords.
         */
        if(fieldcount != i) {
          pstmt = StmtLibrary.getSubrecordsUsingPSDBFIELD_PSRECFIELD_JOIN(this.RECNAME);
          rs = pstmt.executeQuery();

          while(rs.next()) {
            this.fldAndSubrecordTable.put(rs.getInt("FIELDNUM"), rs.getString("FIELDNAME"));
            subRecordNames.add(rs.getString("FIELDNAME"));
            i++;
          }

          if(fieldcount != i) {
            throw new EntVMachRuntimeException("Even after querying for subrecords, field count " +
              "does not match that on PSRECDEFN.");
          }
        }

        pstmt = StmtLibrary.getPSDBFLDLBL(this.RECNAME);
        rs = pstmt.executeQuery();
        rs.next();        // Do nothing with records for now.
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

    public void discoverRecordPC() {

      if(this.hasRecordPCBeenDiscovered) { return; }
      this.hasRecordPCBeenDiscovered = true;

      PreparedStatement pstmt = null;
      ResultSet rs = null;

      try {
        this.recordProgsByFieldTable = new HashMap<String, ArrayList<PeopleCodeProg>>();
        this.orderedRecordProgs = new ArrayList<PeopleCodeProg>();

        pstmt = StmtLibrary.getPSPCMPROG_RecordPCList(PSDefn.RECORD, this.RECNAME);
        rs = pstmt.executeQuery();

        while(rs.next()) {
          PeopleCodeProg prog = new RecordPeopleCodeProg(rs.getString("OBJECTVALUE1"),
              rs.getString("OBJECTVALUE2"), rs.getString("OBJECTVALUE3"));
          prog = DefnCache.getProgram(prog);

          ArrayList<PeopleCodeProg> fieldProgList = this.recordProgsByFieldTable
              .get(rs.getString("OBJECTVALUE2"));
          if(fieldProgList == null) {
            fieldProgList = new ArrayList<PeopleCodeProg>();
          }

          fieldProgList.add(prog);
          this.recordProgsByFieldTable.put(rs.getString("OBJECTVALUE2"), fieldProgList);
          this.orderedRecordProgs.add(prog);
        }
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

  public boolean isTable() {
    return this.RECTYPE == 0;
  }

  public boolean isView() {
    return this.RECTYPE == 1;
  }

  public boolean isSubrecord() {
    return this.RECTYPE == 3;
  }

  public boolean isDerivedWorkRecord() {
    return this.RECTYPE == 2;
  }

  /*
   * TODO: This method is tentative. It may need to use the expanded field list
   * rather than the root field list. It also may be too expansive.
   */
  public boolean hasAnyKeys() {
    for(Map.Entry<String, RecordField> cursor : this.fieldTable.entrySet()) {
      RecordField rf = cursor.getValue();
      if(rf.isKey() || rf.isSearchKey() || rf.isAlternateSearchKey()) {
        return true;
      }
    }
    return false;
  }

  public ArrayList<RecordField> getExpandedFieldList() {
    ArrayList<RecordField> expandedFieldList = new ArrayList<RecordField>();
    for(Map.Entry<Integer, Object> cursor : this.fldAndSubrecordTable.entrySet()) {
      Object obj = cursor.getValue();
      if(obj instanceof RecordField) {
        expandedFieldList.add((RecordField) obj);
      } else {
        Record subrecDefn = DefnCache.getRecord((String) obj);
        expandedFieldList.addAll(subrecDefn.getExpandedFieldList());
      }
    }
    return expandedFieldList;
  }

  public ArrayList<PeopleCodeProg> getRecordProgsForField(String FLDNAME) {
    if(this.recordProgsByFieldTable == null) { return null; }
    return this.recordProgsByFieldTable.get(FLDNAME);
  }
}

